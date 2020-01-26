import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn import tree
from sklearn.metrics import accuracy_score
test_size = 1
clf = tree.DecisionTreeClassifier()
mml_data = pd.read_csv('foo2.csv', sep=';')
print (mml_data)
