suppressMessages(install.packages("caret", repos="http://cran.rstudio.com/", quiet=TRUE))
suppressMessages(install.packages("e1071", repos="http://cran.rstudio.com/", quiet=TRUE))
library(caret)
library(e1071)
data <- read.csv("D:/Donnees/Cours/MIAGE/M2/IDM/MML-classification/org.xtext.example.mml.tests/src/org/xtext/example/mydsl/tests/groupewacquet/resources/vehicle.csv", header=TRUE, sep=",")
data <- data[complete.cases(data), ]
shuffleIndex <- sample(1:nrow(data))
data <- data[shuffleIndex,]
trainSample <- 1: (0.8 * nrow(data))
dataTrain <- data[trainSample, ]
dataTest <- data[-trainSample, ]
model <- svm(Class~., data=dataTrain, gamma=1.1, cross=5)
prediction <- predict(model, dataTest[, 1:ncol(dataTest) - 1])
inPredictTest <- dataTest[,ncol(dataTest)]
matrix <- confusionMatrix(prediction, inPredictTest, mode="everything")
classRep <- matrix$byClass
statsNb <- length(matrix$byClass)
ACCURACY <- matrix$overall["Accuracy"]
cat("ACCURACY :", fill=TRUE)
print(ACCURACY)
fileToWrite <- file("src/org/xtext/example/mydsl/tests/groupewacquet/resources/results/rfiles/test6.mml.SVM.txt")
writeLines(capture.output(cat("ACCURACY=", ACCURACY)), fileToWrite)
close(fileToWrite)
