library(readr)
library(caret)
library(randomForest)
library(LogicReg)
library(e1071)
library(party)
data <- read.csv("output_LAFONT_LEMANCEL_MANDE_RIALET/iris.csv")
predictive <- names(data[dim(data)[2]])
predictors <- "."
formula <- reformulate(termlabels = predictors, response = predictive)
split=0.35
trainIndex <- createDataPartition(data[[predictive]], p=split, list=FALSE)
data_train <- data[ trainIndex,]
data_test <- data[-trainIndex,]
fitControl <- trainControl(method="none")
model <- train(formula, data=data_train,method="rf",trControl=fitControl)
pred <- predict(model,newdata=data_test)
u <- union(pred, data_test[[predictive]])
t <- table(factor(pred, u), factor(data_test[[predictive]], u))
mat <- confusionMatrix(t)
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
