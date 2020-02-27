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
model <- svm(formula=formula, data=data_train, type="C-classification", kernel='linear', cost=1.0)
pred <- predict(model,newdata=data_test)
u <- union(pred, data_test[[predictive]])
t <- table(factor(pred, u), factor(data_test[[predictive]], u))
mat <- confusionMatrix(t)
if (!is.null(dim(mat$byClass)[1])) { print(paste("Balanced Accuracy___",mean(mat$byClass[,"Balanced Accuracy"]),sep="")) } else { print(paste("Balanced Accuracy___",mean(mat$byClass["Balanced Accuracy"]),sep="")) }
if (!is.null(dim(mat$byClass)[1])) { print(paste("Recall___",mean(mat$byClass[,"Recall"],na.rm=TRUE),sep="")) } else { print(paste("Recall___",mean(mat$byClass["Recall"],na.rm=TRUE),sep="")) }
if (!is.null(dim(mat$byClass)[1])) { print(paste("Precision___",mean(mat$byClass[,"Precision"],na.rm=TRUE),sep="")) } else { print(paste("Precision",mean(mat$byClass["Precision"],na.rm=TRUE),sep="")) }
if (!is.null(dim(mat$byClass)[1])) { print(paste("F1___",mean(mat$byClass[,"F1"],na.rm=TRUE),sep="")) } else { print(paste("F1___",mean(mat$byClass["F1"],na.rm=TRUE),sep="")) }
print(paste("Accuracy___",as.double(mat$overall["Accuracy"]),sep=""))
print("Macro Recall non supporté")
print("Macro Precision non supporté")
print("Macro F1 non supporté")
print("Macro Accuracy non supporté")
