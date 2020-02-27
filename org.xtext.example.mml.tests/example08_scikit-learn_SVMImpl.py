import pandas as pd
from sklearn import svm


from sklearn.model_selection import train_test_split
from sklearn import metrics

df = pd.read_csv('ecoli.csv', sep=',')
y = df.iloc[:,-1]
X = df.drop(df.columns[-1],axis=1)


clf = svm.SVR()

train_size = 50/100 
X_train, X_test, y_train, y_test = train_test_split(X, y, train_size=train_size)
clf.fit(X_train, y_train)

res_recall_score = metrics.recall_score(y_test,clf.predict(X_test),average='micro')
print(res_recall_score)

