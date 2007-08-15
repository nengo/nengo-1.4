#!/usr/local/bin/perl5 -w

# $Id: jiv.pl,v 1.6 2002/07/07 15:19:02 cc Exp $
#
# Description: this is a wrapper script for invoking JIV on
#              one or more MNI MINC image volumes.
# Requires: mni-perllib (available from ftp.bic.mni.mcgill.ca)
#
# Copyright (C) 2000, 2001 Steve Robbins and Chris Cocosco
# ({stever,crisco}@bic.mni.mcgill.ca)
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
# License for more details.

# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software Foundation, 
# Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA, 
# or see http://www.gnu.org/copyleft/gpl.html


use strict;

use Getopt::Tabular;
use MNI::Startup qw/ nocputimes/;
use MNI::FileUtilities;
use MNI::PathUtilities;
use MNI::Spawn;
use MNI::MincUtilities qw( :geometry :range);
use MNI::MiscUtilities qw(:all);

# this _needs_ to be defined
my $JIVCode = '/some/path/jiv.jar';

# optional (if not defined, a JVM named "java" will be searched for 
# in the default path) 
my $JavaVM;
#$JavaVM = '/usr/local/jdk118/bin/java';

# optional (define only if required by the JVM...)
my $JavaLib;
#$JavaLib = '/usr/local/jdk118/lib/classes.zip';

# optional...
my $JVMOptions= '-mx120m';

MNI::Spawn::RegisterPrograms( [qw/ minctoraw /] )
    or exit 1;
$JavaVM ||= 'java';
$JavaLib ||= '';
{
    my( $dir, $base, $ext)= MNI::PathUtilities::split_path( $JavaVM);
    my $jvm_fully_qualified= $dir ? 
	$JavaVM : MNI::FileUtilities::find_program( $JavaVM);
    exit 1 unless $jvm_fully_qualified;
    MNI::Spawn::RegisterPrograms( { 'jvm' => $jvm_fully_qualified })
	or exit 1;
}


# --- set the help & usage strings ---
my $help = <<HELP;

A simple wrapper around "JIV": it runs the Java viewer on all 
the minc volumes given as arguments.

Limitations: 
- non-standard direction cosines (that is, rotated coordinate axes)
  are not supported.

HELP

my $usage = <<USAGE;
usage:  $ProgramName [options] mincfile1 [ mincfile2 ...]
     or $ProgramName [options] mincfile1:alias1 [ mincfile2:alias2 ...]

     or $ProgramName [options] --merge mincfile1 mincfile2 [...]
        --merge adds a "combined" view for the next 2 files; it can be
        specified more than once; duplicate files are only displayed once.

        $ProgramName -help to list options
USAGE

Getopt::Tabular::SetHelp( $help, $usage );


# --- process options ---
my $labels = 0;
my $sync = 0;
my $view = 1;
my $force = 0;
my @options = 
  ( @DefaultArgs,     # from MNI::Startup
    ['-labels', 'boolean', 0, \$labels,
     "image data are labels (show pixel values as bytes, and preserve the file\'s \"valid range\") [default: $labels]"],
    ['-sync', 'boolean', 0, \$sync,
     "start with all volume cursors synchronized [default: $sync]"],
    ['-view', 'boolean', 0, \$view,
     "launch viewer [default: $view]"],
    ['-force', 'boolean', 0, \$force, "accept non-standard direction cosines (rotated coordinate axes) [default: $force]"],
  );

GetOptions( \@options, \@ARGV ) 
  or exit 1;
die "$usage\n" unless @ARGV > 0;


# --- process the input files ---
my $config= '';
$config .= "jiv.sync = true\n" if $sync;

MNI::FileUtilities::check_output_path("${TmpDir}/raw/") 
  or exit 1;

my $panel = 0;
my %alias;
foreach (@ARGV) {

    next if /^--merge$/;  # $panel is not incremented
    next if exists $alias{$_};

    my $original= $_;
    my ($in_mnc,$alias) = split(/:/);
    ($in_mnc) = MNI::FileUtilities::check_files( [$in_mnc], 1 ); 
    die unless defined $in_mnc;

    my( $dir, $base, $ext) = 
        MNI::PathUtilities::split_path( $in_mnc, 'last', [qw(gz z Z bz2)]);
    $alias = find_unused_alias($alias || $base);
    $alias{$original}= $alias;

    my( @start, @step, @length, @dir_cosines, @dimorder)= 
	( (), (), (), (), ());
    volume_params( $in_mnc, \@start, \@step, \@length, \@dir_cosines, undef);

    my( @irange)= volume_minmax( $in_mnc);

    my( $order, $perm)= get_dimension_order( $in_mnc);
    my( @dim_names)= qw/ x y z/;
    @dimorder= map { $dim_names[$_] } @$order;

    # TODO/FIXME: allow for some slop (+/- 5%) in the test ...
    unless( nlist_equal( \@dir_cosines, [ 1,0,0, 0,1,0, 0,0,1 ]) ) {
	if( $force) {
	    warn "\nWARNING! $in_mnc : non-standard direction cosines : world coordinates will not be available!\n\n";
	    $config .= "jiv.world_coords = false\n";
	}
	else {
	    die "$in_mnc : non-standard direction cosines (that is, rotated coordinate axes) are not supported! Use -force to override ... \n";
	}
    }

    my $header= '';
    $header .= "size   :  @length\n";
    $header .= "start  :  @start\n";
    $header .= "step   :  @step\n";
    $header .= "order  :  @dimorder\n\n";

    if( $labels) { 
	$config .= "jiv.byte_values = true\n";
	# NB: this may not be correct... (?)
	$header .= "imagerange  : 0 255\n";
    }
    else {
	$header .= "imagerange  :  @irange\n";
    }

    my $out_raw = "${TmpDir}/raw/$dir/$base";
    my $out_header = "${TmpDir}/raw/$dir/${base}.header";
    MNI::FileUtilities::check_output_path( $out_header) or exit 1;
    write_file( $out_header, $header );

    # NB: it's safer to '-norm' all the time (otherwise minctoraw
    # might decide to scale differently data from different slices --
    # e.g. if the volume has different min-max values for each
    # slice...)
    my $norm_options= $labels ? "-norm" : "-norm -range 0 255";

    Spawn( "minctoraw ${norm_options} -byte $in_mnc",
	   stdout => $out_raw,
	   clobber => $Clobber)
      unless( (-e $out_raw) && !$Clobber);
    
    $config .= "jiv.panel.$panel = $alias\n";
    $config .= "$alias = $out_raw\n";
    $config .= "${alias}.header = $out_header\n";
    ++$panel;
}

for( my $i= 0; $i < @ARGV; ++$i ) {
    $_= $ARGV[$i];
    next unless /^--merge$/ ;

    $config .= "jiv.panel.${panel}.combine = $alias{$ARGV[$i+1]} $alias{$ARGV[$i+2]}\n";
    ++$panel;
}

if( $labels) { 
    $config .= "jiv.byte_values = true\n";
}

# --- set up config and html files then run appletviewer ---

write_file( "${TmpDir}/jiv.conf", $config );

print <<_EOM_;


**** JIV (Chris Cocosco <crisco\@bic.mni.mcgill.ca>)     ****
****                                                    ****
**** use right mouse-button to access the pop-up menus  ****


_EOM_

my $cp= $JIVCode;
$cp .= ":$JavaLib"  if $JavaLib;
Spawn( "jvm $JVMOptions -classpath $cp jiv.Main ${TmpDir}/jiv.conf" )
    if $view;

# --- end of script ! ---



sub write_file {
    my( $name, $text ) = @_;
    open( OUT, ">$name" )
      or die "error creating `$name' ($!)\n";
    print OUT $text;
    close( OUT )
      or die "error closing file `$name' ($!)\n";
}



my %used_aliases;
sub find_unused_alias {
    my( $base, $count ) = @_;

    my $alias;
    if (defined $count) {
	$alias = "$base-$count";
    } else {
	$alias = $base;
	$count = 0;
    }

    if ( exists $used_aliases{$alias} ) {
	return find_unused_alias( $base, $count + 1 );
    } else {
	$used_aliases{$alias} = 1;
	return $alias;
    }
}
	
