suppressMessages(install.packages("caret", repos="http://cran.rstudio.com/", quiet=TRUE))
suppressMessages(install.packages("e1071", repos="http://cran.rstudio.com/", quiet=TRUE))
suppressMessages(install.packages("nnet", repos="http://cran.rstudio.com/", quiet=TRUE))
library(nnet)
library(caret)
library(e1071)
data <- read.csv("D:/Donnees/Cours/MIAGE/M2/IDM/MML-classification/org.xtext.example.mml.tests/src/org/xtext/example/mydsl/tests/groupewacquet/resources/iris.csv", header=TRUE, sep=",")
data <- data[complete.cases(data), ]
shuffleIndex <- sample(1:nrow(data))
data <- data[shuffleIndex,]
trainSample <- 1: (0.8 * nrow(data))
dataTrain <- data[trainSample, ]
dataTest <- data[-trainSample, ]
classNb <- length(dataTest$variety~.)
trControl <- trainControl(method="cv", number=10)
if (classNb <= 2) {
    model <- train(variety~., data=dataTrain, method="glm", family=binomial, trControl=trControl, na.action=na.pass)
} else {
    model <- train(variety~., data=dataTrain, method="multinom", trace=FALSE, trControl=trControl, na.action=na.pass)
}
if (classNb <= 2) {
    prediction <- predict(model, dataTest[, 1:ncol(dataTest) - 1], type="response")
} else {
    prediction <- predict(model, dataTest[, 1:ncol(dataTest) - 1])
}
inPredictTest <- dataTest[,ncol(dataTest)]
matrix <- confusionMatrix(prediction, inPredictTest, mode="everything")
classRep <- matrix$byClass
statsNb <- length(matrix$byClass)
ACCURACY <- matrix$overall["Accuracy"]
cat("ACCURACY :", fill=TRUE)
print(ACCURACY)
fileToWrite <- file("src/org/xtext/example/mydsl/tests/groupewacquet/resources/results/rfiles/test3.mml.LogisticRegression.txt")
writeLines(capture.output(cat("ACCURACY=", ACCURACY)), fileToWrite)
close(fileToWrite)
