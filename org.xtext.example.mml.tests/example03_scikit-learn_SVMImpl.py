import pandas as pd
from sklearn import svm


from sklearn.model_selection import cross_validate
from sklearn import metrics

df = pd.read_csv('iris.csv', sep=',')
y = df.iloc[:,-1]
X = df.drop(df.columns[-1],axis=1)


clf = svm.SVR()

scoring_metrics = ['recall_micro',]

cv_results = cross_validate(clf, X, y, cv= 5, scoring = scoring_metrics)

 #Result of recall_micro metric 
res_recall_score = cv_results['test_recall_micro'].mean()
print(res_recall_score)

