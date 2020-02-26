library(readr)
library(caret)
library(randomForest)
library(LogicReg)
library(e1071)
library(party)
data <- read_csv("output_LAFONT_LEMANCEL_MANDE_RIALET/iris.csv")
predictive <- names(data[dim(data)[2]])
predictors <- "."
formula <- reformulate(termlabels = predictors, response = predictive)
fitControl <- trainControl(method="cv", number=10)
data_train <- data
data_test <- data
model <- svm(formula=formula, data=data_train, type="C-classification", kernel='linear', cost=1.0)
pred <- predict(model,newdata=data_test)
mat <- confusionMatrix(table(pred,data_test[[predictive]]))
print("Balanced Accuracy")
if (!is.null(dim(mat$byClass)[1])) { print(mean(mat$byClass[,"Balanced Accuracy"])) } else { print(mean(mat$byClass["Balanced Accuracy"])) }
print("Recall")
if (!is.null(dim(mat$byClass)[1])) { print(mean(mat$byClass[,"Recall"],na.rm=TRUE)) } else { print(mean(mat$byClass["Recall"],na.rm=TRUE)) }
print("Precision")
if (!is.null(dim(mat$byClass)[1])) { print(mean(mat$byClass[,"Precision"],na.rm=TRUE)) } else { print(mean(mat$byClass["Precision"],na.rm=TRUE)) }
print("F1")
if (!is.null(dim(mat$byClass)[1])) { print(mean(mat$byClass[,"F1"],na.rm=TRUE)) } else { print(mean(mat$byClass["F1"],na.rm=TRUE)) }
print("Accuracy")
print(mat$overall["Accuracy"])
print("Macro Recall non supporté")
print("Macro Precision non supporté")
print("Macro F1 non supporté")
print("Macro Accuracy non supporté")
