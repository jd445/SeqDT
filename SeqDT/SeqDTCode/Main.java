package SeqDTCode;

import java.util.ArrayList;

import java.util.Collections;

public class Main {
	public static void main(String[] args) throws Exception {
		int maxL = 4;
		// maximum length
		int g = 1;
		// gap constraint
		double threshold = 0.1;
		// maximum value of Gini index in one node
		int minNum = 2;
		// minimum number of sequence in one node
		double minSplit = 0.0;
		// minimum value of decreased impurity generated by segmentation
		int depth = 0;
		// maximum depth of the tree
		boolean pru = true;
		// determine whether to prune

		// 10 folder cross validation
		Main ten_folder = new Main();
		ReadFile f = new ReadFile();
		ArrayList<String> Totaldata = new ArrayList<String>();
		Totaldata = f.Read("./robot.txt");
		AverageFoldRes testRes;
		float AveAccuracy = 0;
		float AveMCC = 0;
		float AveSensitivity = 0;
		float AveSpecificity = 0;
		for (int i = 0; i < 5; i++) {
			System.out.println("Times " + i);
			testRes = ten_folder.kFoldValidation(Totaldata, g, maxL, threshold, minNum, minSplit, depth, pru, 10);
			AveAccuracy += testRes.AveAccuracy;
			AveMCC += testRes.AveMCC;
			AveSensitivity += testRes.AveSensitivity;
			AveSpecificity += testRes.AveSpecificity;
		}
		System.out.println(AveAccuracy / 5 + " " + AveMCC / 5 + " " + AveSensitivity / 5 + " " + AveSpecificity / 5);
		// Original method
		// Main m = new Main();
		// ReadFile r = new ReadFile();
		// ArrayList<String> Tdata = new ArrayList<String>();
		// // training set
		// Tdata = r.Read("./train.txt");
		// Node root = new Node();
		// System.out.println("g:" + g + " maxL:" + maxL + " threshold:" + threshold + "
		// minNum:" + minNum + " depth:"
		// + depth + " pru:" + pru);
		// // create the decision tree
		// root = m.Train(Tdata, g, maxL, threshold, minNum, minSplit, depth, pru);
		// // testing set
		// Tdata = r.Read("./test.txt");
		// // test the decision tree constructed
		// float acc = m.Test(Tdata, g, root);
		// System.out.println("acc:" + acc);
	}

	public Main() {
	}

	/**
	 * build a decision tree based on the training set
	 * 
	 * @param train: training set
	 * @return root node of decision tree
	 * @throws Exception
	 */
	// Junie Dong：2021-11-13 k fold validation method added.
	public AverageFoldRes kFoldValidation(ArrayList<String> train, int g, int maxL, double threshold, int minNum,
			double minSplit, int depth, boolean pru, int k) throws Exception {
		// Tree[] trees = new Tree[k];
		Node[] roots = new Node[k];
		ArrayList<ArrayList<String>> folder = new ArrayList<ArrayList<String>>();
		Collections.shuffle(train);
		int oneFolderSize = train.size() / k;
		for (int i = 0; i < train.size(); i = i + oneFolderSize) {
			ArrayList<String> BUFF = new ArrayList<String>();
			for (int j = 0; j < oneFolderSize; j++) {
				BUFF.add(train.get(i + j));
			}
			folder.add(BUFF);
		}
		MeasureIndex[] acc = new MeasureIndex[k];
		for (int j = 0; j < k; j++) {
			System.out.println("folder " + (j + 1));
			ArrayList<ArrayList<String>> trainset;
			trainset = new ArrayList<>(folder);
			ArrayList<String> Testset = trainset.get(j);
			trainset.remove(Testset);
			ArrayList<String> Trainset = new ArrayList<String>();

			for (ArrayList<String> arrayList : trainset) {
				Trainset.addAll(arrayList);
			}
			// System.out.println(Testset.size() + " " + Trainset.size());
			// train each folder
			roots[j] = Train(Trainset, g, maxL, threshold, minNum, minSplit, depth, pru);
			acc[j] = Test(Testset, g, roots[j]);
			// System.out.println("Accuracy = " + acc[j].getAccuracy() + " MCC = " +
			// acc[j].getMCC() + " Sensitivity = "
			// + acc[j].getSensitivity() + " Specificity = " + acc[j].getSpecificity());
		}
		// float sum = 0;
		// for (float f : acc) {
		// sum = sum + f;
		// }
		// System.out.println("average acc = " + sum / k);
		float AveAccuracy = 0;
		float AveMCC = 0;
		float AveSensitivity = 0;
		float AveSpecificity = 0;
		for (MeasureIndex measureIndex : acc) {
			AveAccuracy = AveAccuracy + measureIndex.getAccuracy();
			AveMCC = AveMCC + measureIndex.getMCC();
			AveSensitivity = AveSensitivity + measureIndex.getSensitivity();
			AveSpecificity = AveSpecificity + measureIndex.getSpecificity();
		}
		AveAccuracy = AveAccuracy / k;
		AveMCC = AveMCC / k;
		AveSensitivity = AveSensitivity / k;
		AveSpecificity = AveSpecificity / k;
		AverageFoldRes averageFoldRes = new AverageFoldRes(AveAccuracy, AveMCC, AveSensitivity, AveSpecificity);
		return averageFoldRes;
	}

	public Node Train(ArrayList<String> train, int g, int maxL, double threshold, int minNum, double minSplit,
			int depth, boolean pru) throws Exception {

		Tree tree = new Tree(g, maxL, threshold, minNum, minSplit, depth, pru);
		Node root = new Node();
		root = tree.createRoot(train);
		return root;
	}

	/**
	 * use a decision tree to classify sequences
	 * 
	 * @param test: the testing set
	 * @param root: the root node of the decision tree
	 * @return classification accuracy
	 */
	// Junie Dong：2021-11-16 measureindex method addded.
	public MeasureIndex Test(ArrayList<String> test, int g, Node root) {
		Classification cf = new Classification(g);
		int TP = 0;
		int TN = 0;
		int FP = 0;
		int FN = 0;
		for (String str : test) {
			String[] s = str.split("\t");
			String label = s[0];
			String[] dataInstanceForTesting = s[1].split(" ");
			String TempLabel = cf.getLabel(root, dataInstanceForTesting);
			if (label.equals("1") && TempLabel.equals("1")) {
				TP++;
			} else if (label.equals("1") && TempLabel.equals("0")) {
				FN++;
			} else if (label.equals("0") && TempLabel.equals("1")) {
				FP++;
			} else if (label.equals("0") && TempLabel.equals("0")) {
				TN++;
			}
			// TempLabel 预测标签
			// label 数据标签
			/*
			 * here is the original acc test value return way if (TempLabel.equals(label)) {
			 * correct++; } else { // System.out.println(str); }
			 */
		}
		MeasureIndex TestResults = new MeasureIndex(TP, TN, FP, FN);
		return TestResults;
	}
}