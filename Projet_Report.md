
# IDM Report

  Our team is composed of Robin LEMANCEL, Yohann RIALET, Guillaume MANDE and Jérémy LAFONT.

Repository Git : [https://github.com/Youlounn/MML-classification.git](https://github.com/Youlounn/MML-classification.git)

  
  

### R Language (Robin)

  

Doing machine learning with R was quite difficult because we don’t really understand what we are doing and we don’t know the syntax of the language.

We focused on doing a good compiler but not on doing good machine learning algorithms.

There is an executable program which permits to install the necessary packages for R in output_LAFONT_LEMANCEL_MANDE_RIALET/executable/.

This program needs to be executed one time. After that, you can compile MML programs with R.

It is also necessary to add the path to Rscript.exe in your system environment variable PATH.

  

![](https://lh5.googleusercontent.com/f8E_2YstICF6J1Zb00nqcFliBfNqFStFt3aAKqhUQFr1bd0U_rl4_DMvROGU6xhbQMbutn3Crqm4hq5_ZqaBKfxn-xrCV4sf9mwOXfPWn1H2fm17WxEUQQcsfgCSHMZAe2k_iWBa)

### Weka (Jeremy / Yohann)

This framework was the easiest to implement. On one hand because we are used to working with Java, which makes the syntax and the approach to the problem easier to grasp.

 On the other hand, we had already implemented the compiler to scikit-learn, so we had already understood the logic and this allowed us to go faster in the implementation of this compiler.

In order to test this framework, you must first have Weka installed on your machine. The Weka library has been added to the project in order to be able to compile the .java files and then run them, but the framework must be installed on the computer.

### ![](https://lh5.googleusercontent.com/tB0g9CzhTFoSUEZ9j5scFM_rxiblpPbjAvuWm-XI_8CO9AQAsD2S97yiisN_ywbfX2SOJavIfd_9XWYeSIHbF9LF-0KRWajs6bAh8h47LwmP4TGJuzEjjIm_JUielcDDsokcXVNS)

  

### Python (Yohann / Robin)

This framework was one of the most difficult to put in place because it was the first one we had to create. Also because we don't know much about python syntax.

Luckily there was an example of python code compiled to know what kind of output we had to go for.

Once again, you need to have Python installed on the computer in order to run the programs compiled with this framework.

  

### ![](https://lh3.googleusercontent.com/mjRAL-ZN-MA6QMq2x6WNZQI2oG2Z0f9M-3vPD6QBN6_JBpSKCGlNpsIsoBVKI8sRCpLSsi3sjCaQzk-BOqSOFmLCcuuduOEKA-P9j2WK06fHJFxo7Bzk8UxvJueFS8GZUqJuFhAL)

### XGBoost (Guillaume)

  

This part can be used thanks to the xgboost branch created in Github.

  

For this part of the project, it is kinda difficult to proceed and develop with the XGBoost package. First of all because there is not much of documentation about it, then it does not take care of every algorithms.

It manages the Classifier pretty well, but not the others at all. And it has issues to deal with .csv files, it prefers to deal with libsvm files.

That is why this part is not well implemented but with all the work and research we have done that is the best we can do for now by far.

  
  

### Test cases

  

To test our different compilers, we did a lot of test cases which permit to test each functionality, each algorithm, and each validation on each framework. We only test the good execution of the compiled program (if the exitCode of the program equals 0, the execution is good). We don’t test the coherence of the values returned by the compiled programs.

But we use these values printing them in the java System console and creating two different csv files, one sorted by the score of the metrics (scores.csv), the other sorted by the execution time (times.csv).

  
  
  

**1.  Sur vos jeux de données, quel framework+algorithme est le mieux classé (en comparaison d’autres frameworks) en termes de temps d’exécution? de précision?**
    

Regarding the classification score, we can see that the algorithm “RandomForest” used by “Weka” is the one giving us the highest precision scores (100%).

  

![](https://lh3.googleusercontent.com/hghigoHG4i0v46OAguLN47pXapRmSviFJcA8bhqVrD0IKKdcvwocQFFofW3VhDa7x3Dt25GpcQan3gaZCCpG669WsVWatkxGptYax3wu_jXNWKxOHYkPTqn0BcTPhQjHhEem5Q75)

Regarding execution time, the algorithm “Logistic Regression” from “Weka” is the one giving us the best execution time with around 2000ms in average.

  

### ![](https://lh5.googleusercontent.com/tB0g9CzhTFoSUEZ9j5scFM_rxiblpPbjAvuWm-XI_8CO9AQAsD2S97yiisN_ywbfX2SOJavIfd_9XWYeSIHbF9LF-0KRWajs6bAh8h47LwmP4TGJuzEjjIm_JUielcDDsokcXVNS)

  

**2.  Parmi les frameworks et algorithmes de machine learning, y a-t-il des implémentations significativement plus lentes/précises que d’autres?**
    

  

We noticed that the algorithms RandomForest and DecisionTree in “R” are giving us the worst numbers, in terms of precision score AND execution time.

  

End of precision classification

![](https://lh4.googleusercontent.com/sQ2klCRs5EFyVibrWOIIvnn2C8acYWhMFCimclADSV40gmt64n1sKmpVc4o4mSphcq03QQ-r3nCotbzCA6qhPRt5xdLyQNepU1MxPMUP8sDkkF5CAEuxrvPHTvxZlnboukFNoIsP)

End of run-time classification

![](https://lh4.googleusercontent.com/JZGq6Qz4pTeIaG1z71YF7iu_ZuzFTLrXD7tx_x3yZmDdmLIVEPeqdbHX_4_F1S9kfEIpqeI8zUeAxVHw2HioKrQ1TtTiCHj8pm3CJOVlSmW3vgdfMg07T-2o7usVuAJAKwoOlsqN)

  

**3.  Etant donné un algorithme de machine learning (e.g., decision tree), est-ce qu’on observe des différences (temps d’exécution/précision) entre les frameworks?**
    

  

Yes, we can definitely see important differences in terms of execution time with the algorithm DecisionTree for example. It is taking more than 3.5 seconds of execution for each test done while using R when it is taking roughly 1.6 seconds when using Weka (putting it in first place).

  

![](https://lh6.googleusercontent.com/MLoBZOJFDonS0Up2mdHbP-_WzQoPszlvI4c9L1vqzPzBicbAQq4JR2WuVeMqjtQUHErMjna5nQLMd8tNWcbRujuWYASG133kaCouH7rOa0c270p8UOB4Gchrjqkt5XXi8aOtQCf7)

  
  

Regarding precision, Weka is getting the best precision scores, and holds all first places.

  

**4.  Y a-t-il des jeux de données plus difficiles à traiter (en termes de précision/temps d’exécution)?**
    

  

We did not succeed in making the datasets modular throughout our testing.

  
  
  
  
  

**5.  Au vu des résultats, quel framework de machine learning recommanderiez-vous?**
    

  

After making all the different “rankings”, we would recommend using Weka. It is indeed getting the best scores whether we are looking at precision or execution time.

  

Start of precision classification

![](https://lh6.googleusercontent.com/D337F1I1_qaypwYan--hFlnq7k6ZtDu2rR6ybzgqr-N2aMzsSrL0CRlhAUa7PJBTxQEbf13ztnEUy2KkTw5TzTwwozH_NcCJQuLrzs_q9VfdMmbWpfzMO1qJiewTVaLNd0RkXv22)

  

Start of run-time classification

![](https://lh5.googleusercontent.com/f-YDsfBl5chysdQl8IsamjgYcyf9PYDy0OEVvRF8vYxYzBUdMTG-sna_SvnL6sFnZoOdxgRM8iRIc8V6b2LOKR19NyIII_-3QFa4CAS1r7vrCajnZR6ninG_STOP1Bf0rlxQvMCg)
