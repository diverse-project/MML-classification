package output_LAFONT_LEMANCEL_MANDE_RIALET;
import java.io.File;
import java.util.Random;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.converters.CSVLoader;
import weka.core.Instances;
public class Mml_1 {
	public static void main(String[] args) throws Exception {
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(System.getProperty("user.dir")+"\\output_LAFONT_LEMANCEL_MANDE_RIALET\\iris.csv"));
		Instances data = loader.getDataSet();
		data.randomize(new Random());

		data.setClassIndex(data.numAttributes()-1);
		int trainSize = (int) Math.round(data.numInstances() * 0.65);
		int testSize = data.numInstances() - trainSize;
		Instances train = new Instances(data, 0, trainSize);
		Instances test = new Instances(data, trainSize, testSize);
		Classifier cls = new RandomForest();
		cls.buildClassifier(data);
		Evaluation eval = new Evaluation(train);
		eval.evaluateModel(cls, test);
		System.out.println("La métrique balanced_accuracy n'est pas supportée avec Weka.");
		System.out.println("Recall = "+eval.weightedRecall()*100);
		System.out.println("Precision ="+eval.weightedPrecision()*100);
		System.out.println("F1 = "+eval.weightedFMeasure()*100);
		System.out.println("Accuracy = "+eval.pctCorrect());
		System.out.println("La métrique macro_recall n'est pas supportée avec Weka.");
		System.out.println("La métrique macro_precision n'est pas supportée avec Weka.");
		System.out.println("La métrique macro_F1 n'est pas supportée avec Weka.");
		System.out.println("La métrique macro_accuracy n'est pas supportée avec Weka.");
	}
}