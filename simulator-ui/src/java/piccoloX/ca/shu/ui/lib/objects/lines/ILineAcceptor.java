package ca.shu.ui.lib.objects.lines;

public interface ILineAcceptor {
	
	public boolean connect(LineEnd lineEnd);
	
	public void disconnect();
	
}
