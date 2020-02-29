import pandas as pd
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import recall_score
mml_data = pd.read_csv('foo2.csv', sep=',')
Y = mml_data[mml_data.columns[len(mml_data.columns)-1]] 
X = mml_data.drop(columns=mml_data.columns[len(mml_data.columns)-1])
algo = DecisionTreeClassifier()
test_size =0
X_train, X_test, Y_train, Y_test = train_test_split(X, Y, test_size=test_size)
algo.fit(X_train, y_train)
 y_pred = clf.predict(X_test)
 Y = y_test
recall = recall_score(Y, y_pred,average='micro' )
print(recall)
