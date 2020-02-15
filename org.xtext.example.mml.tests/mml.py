import pandas as pd
from sklearn import tree


from sklearn import metrics
mml_data = pd.read_csv('foo2.csv', sep=';')

clf = tree.DecisionTreeClassifier()


test_size = 1
