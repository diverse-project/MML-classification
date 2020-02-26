import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn import tree
import os
from sklearn.ensemble import RandomForestClassifier 
from sklearn.model_selection import cross_validate
mml_data = pd.read_csv(os.path.abspath(os.path.dirname(__file__))+'\iris.csv', sep=',',encoding='utf-8')
X = mml_data.iloc[:, :-1]
Y = mml_data.iloc[:,-1]
clf = RandomForestClassifier(n_estimators=100)
scoring = ['balanced_accuracy','recall_micro','precision_micro','f1_micro','accuracy','recall_macro','precision_macro','f1_macro',]
scores = cross_validate(clf,X, Y, scoring=scoring,cv=10)
print('balanced_accuracy : ')
print(sum(scores['test_balanced_accuracy']) / len(scores['test_balanced_accuracy']))
print('recall_micro : ')
print(sum(scores['test_recall_micro']) / len(scores['test_recall_micro']))
print('precision_micro : ')
print(sum(scores['test_precision_micro']) / len(scores['test_precision_micro']))
print('f1_micro : ')
print(sum(scores['test_f1_micro']) / len(scores['test_f1_micro']))
print('accuracy : ')
print(sum(scores['test_accuracy']) / len(scores['test_accuracy']))
print('macro_recall : ')
print(sum(scores['test_recall_macro']) / len(scores['test_recall_macro']))
print('macro_precision : ')
print(sum(scores['test_precision_macro']) / len(scores['test_precision_macro']))
print('macro_F1 : ')
print(sum(scores['test_f1_macro']) / len(scores['test_f1_macro']))
