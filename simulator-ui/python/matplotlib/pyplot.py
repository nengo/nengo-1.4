from ca.nengo.util.impl import TimeSeriesImpl
from ca.nengo.model import Units
from ca.nengo.plot.impl import DefaultPlotter


class plot_data_class:
    time_data = []
    value_data = []
    fig_num = 1


class data_class:
    values = None
    label = ""
    def __init__(self, values, label):
        self.values = values
        self.label = label


pd = plot_data_class()


def reset():
    pd.fig_num = 1
    for i in range(len(pd.time_data)):
        pd.time_data.pop()
        pd.value_data.pop()


def plot(time, data, label):
    pd.time_data.append(time)
    pd.value_data.append(data_class(data, label))


def ylim(limits):
    pass


def legend():
    pass


def figure(fig_num = -1):
    if( fig_num < 0 ):
        temp_fig_num = pd.fig_num + 1
    else:
        temp_fig_num = fig_num
    reset()
    pd.fig_num = temp_fig_num


def show():
    plotter = DefaultPlotter()
    time_dict = {}
    value_dict = {}
    for i in range(len(pd.time_data)):
        found_time = False
        for key in time_dict.keys():
            if( time_dict[key] == pd.time_data[i] ):
                value_dict[key].append(pd.value_data[i])
                found_time = True
        if( not found_time ):
            key = len(time_dict)
            time_dict[key] = pd.time_data[i]
            value_dict[key] = [pd.value_data[i]]

    for key in time_dict.keys():
        values = [[] for _ in time_dict[key]]
        labels = []
        for value in value_dict[key]:
            for d in range(len(value.values[i])):
                values = [values[i] + [value.values[i][d]] for i in range(len(values))]
                labels.append(value.label + "[%d]" % d)

        ts = TimeSeriesImpl(time_dict[key], values, [Units.UNK] * len(value_dict[key]))
        for i,label in enumerate(labels):
            ts.setLabel(i, label)

        plotter.doPlot(ts, "Figure %d.%d" % (pd.fig_num, key+1))
