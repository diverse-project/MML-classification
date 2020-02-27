suppressMessages(install.packages("caret", repos="http://cran.rstudio.com/", quiet=TRUE))
suppressMessages(install.packages("e1071", repos="http://cran.rstudio.com/", quiet=TRUE))
suppressMessages(install.packages("rpart", repos="http://cran.rstudio.com/", quiet=TRUE))
library(rpart)
library(caret)
library(e1071)
data <- read.csv("D:/Donnees/Cours/MIAGE/M2/IDM/MML-classification/org.xtext.example.mml.tests/src/org/xtext/example/mydsl/tests/groupewacquet/resources/vehicle.csv", header=TRUE, sep=",")
data <- data[complete.cases(data), ]
shuffleIndex <- sample(1:nrow(data))
data <- data[shuffleIndex,]
trainSample <- 1: (0.8 * nrow(data))
dataTrain <- data[trainSample, ]
dataTest <- data[-trainSample, ]
model <- rpart(Class~., data=dataTrain, method='class')
prediction <- predict(model, dataTest[, 1:ncol(dataTest) - 1], type='class')
inPredictTest <- dataTest[,ncol(dataTest)]
matrix <- confusionMatrix(prediction, inPredictTest, mode="everything")
classRep <- matrix$byClass
statsNb <- length(matrix$byClass)
if (statsNb > 11) {
F1 <- classRep[TRUE,c("F1")]
} else {
F1 <- classRep[c("F1")]
}
cat("F1 :", fill=TRUE)
print(F1)
fileToWrite <- file("src/org/xtext/example/mydsl/tests/groupewacquet/resources/results/rfiles/test2.mml.DT.txt")
writeLines(capture.output(cat("F1=", F1)), fileToWrite)
close(fileToWrite)
