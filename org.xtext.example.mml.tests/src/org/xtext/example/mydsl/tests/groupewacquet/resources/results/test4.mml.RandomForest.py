import pandas as pd
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, confusion_matrix
from sklearn.model_selection import GridSearchCV
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier
from sklearn.svm import LinearSVC
from sklearn.svm import OneClassSVM
from sklearn.svm import NuSVC
from sklearn.metrics import accuracy_score
from sklearn.metrics import balanced_accuracy_score
from sklearn.metrics import f1_score
from sklearn.metrics import precision_score
from sklearn.metrics import recall_score
df = pd.read_csv('D:/Donnees/Cours/MIAGE/M2/IDM/MML-classification/org.xtext.example.mml.tests/src/org/xtext/example/mydsl/tests/groupewacquet/resources/iris.csv', sep=',')
features = list(df.columns[:4])
X = df.drop('variety', axis=1)
y = df['variety']
parameters = {}
y_test = y;
classifier = GridSearchCV(RandomForestClassifier(), parameters, cv=20)
classifier.fit(X=X, y=y)
tree_model = classifier.best_estimator_
y_pred = classifier.predict(X)
text_file = open("src/org/xtext/example/mydsl/tests/groupewacquet/resources/results/pythonfiles/test4.mml.RandomForest.txt", "w")
print("accuracy_score= " + str(accuracy_score(y_test, y_pred)),flush=True)
text_file.write("accuracy_score= " + str(accuracy_score(y_test, y_pred)))
text_file.close()
