import random
import time


def dist(x1, x2):
    return sum([(x1[i] - x2[i]) ** 2 for i in range(len(x2))])


class Map:
    def __init__(self, data, rows, cols):
        self.data = data
        self.rows = rows
        self.cols = cols
        self.N = len(data)
        self.map = range(self.N)
        self.improvements = 0

    def improve(self):
        self.improvements += 1
        if self.N <= 1:
            return 0
        total = 0
        quit_time = time.clock() + 0.5
        count = 0
        for i in range(self.N):
            count += 1
            j = random.randrange(self.N)
            if i != j:
                total += self.swap_if_better(i, j)
            if time.clock() > quit_time:
                break
        return total / count

    def swap_if_better(self, i, j):
        N = self.N
        data = self.data
        map = self.map
        x1 = data[map[i]]
        x2 = data[map[j]]
        score1 = 0
        score2 = 0
        for x in (-1, 0, 1):
            for y in (-self.cols, 0, self.cols):
                if x != 0 or y != 0:
                    score1 += dist(x1, data[map[(i + x + y) % N]])
                    score1 += dist(x2, data[map[(j + x + y) % N]])
        map[i], map[j] = map[j], map[i]
        for x in (-1, 0, 1):
            for y in (-self.cols, 0, self.cols):
                if x != 0 or y != 0:
                    score2 += dist(x2, data[map[(i + x + y) % N]])
                    score2 += dist(x1, data[map[(j + x + y) % N]])
        if score1 <= score2:
            map[i], map[j] = map[j], map[i]
            return 0
        else:
            return 1


#this is here to provide the functionality of a global cache but without cluttering the global namespace
class MapCache:
    def __init__(self):
        self.cache = {}

    def get(self, ensemble, rows, cols):
        key = (ensemble, rows, cols)
        if key not in self.cache:
            self.cache[key] = Map(ensemble.encoders, rows, cols)
        return self.cache[key]
