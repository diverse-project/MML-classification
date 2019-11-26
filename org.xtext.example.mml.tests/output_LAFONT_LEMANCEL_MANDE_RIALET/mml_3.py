import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn import tree
from sklearn.linear_model import LogisticRegression 
from sklearn.model_selection import cross_validate
mml_data = pd.read_csv('iris.csv', sep=',')
X = mml_data.iloc[:, :-1]
print('x:')
print(X)
Y = mml_data.iloc[:,-1]
print('y:')
print(Y)
clf = LogisticRegression()
scoring = ['accuracy'] 
scores = cross_validate(clf,X, Y, scoring=scoring,cv=5)
print('accuracy : ')
print(scores['test_accuracy'])
