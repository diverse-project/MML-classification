import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn import tree
from sklearn.ensemble import RandomForestClassifier 
from sklearn.metrics import balanced_accuracy_score
from sklearn.metrics import recall_score
from sklearn.metrics import precision_score
from sklearn.metrics import f1_score
from sklearn.metrics import accuracy_score
from sklearn.metrics import recall_score
from sklearn.metrics import precision_score
from sklearn.metrics import f1_score
mml_data = pd.read_csv('iris.csv', sep=',')
X = mml_data.iloc[:, :-1]
Y = mml_data.iloc[:,-1]
clf = RandomForestClassifier(n_estimators=100)
test_size = 0.30
X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=test_size)
clf.fit(X_train, y_train)
accuracy = balanced_accuracy_score(y_test, clf.predict(X_test))
print('balanced accuracy :')
print(accuracy)
recall = recall_score(y_test, clf.predict(X_test),average='micro')
print('recall :')
print(recall)
precision = precision_score(y_test, clf.predict(X_test),average='micro')
print('precision :')
print(precision)
F1 = f1_score(y_test, clf.predict(X_test),average='micro')
print('F1 :')
print(F1)
accuracy = accuracy_score(y_test, clf.predict(X_test))
print('accuracy :')
print(accuracy)
recall = recall_score(y_test, clf.predict(X_test),average='macro')
print('recall :')
print(recall)
precision = precision_score(y_test, clf.predict(X_test),average='macro')
print('macro precision :')
print(precision)
F1 = f1_score(y_test, clf.predict(X_test),average='macro')
print('macro F1 :')
print(F1)
