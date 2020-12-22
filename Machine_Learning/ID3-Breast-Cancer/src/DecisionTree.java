import java.util.*;

public class DecisionTree {

    private final Integer MIN_ROWS = 10;
    private final Integer CLASS_COLUMN_INDEX = 0;
    private final Integer MAX_VISITED_COLUMNS = 2 * DataMapping.FEATURES.length / 3;
    private final Integer COLUMNS_COUNT = DataMapping.FEATURES.length;

    private BitSet visitedColumns;
    private int rowCount;
    private String[][] table;
    private Node rootNode;

    DecisionTree(String[][] table, Integer rows){
        this.rowCount = rows;
        this.table = table;
        this.rootNode = new Node();
        this.visitedColumns = new BitSet();
        BitSet unvisitedRows = new BitSet(this.rowCount);
        for (int i = 0; i < this.rowCount; i++){
            unvisitedRows.set(i);
        }

        this.build(this.rootNode, unvisitedRows);
    }

    private void build(Node node, BitSet unvisitedRows){
        Feature feature = this.getBestFeature(unvisitedRows);

        if (feature == null){
            node.setLeaf(true);
            String className = this.getClassForUnvisitedRows(unvisitedRows);
            node.setFeatureValue(className);
            return;
        }

        node.setFeatureValue(feature.getFeatureName());
        node.setIndex(feature.getIndex());

        this.visitedColumns.set(feature.getIndex());
        Map<String, ValueInfo> values = feature.getValues();

        for (String valueName :
                values.keySet()) {
            ValueInfo currValue = values.get(valueName);
            Map<String, Integer> classes = currValue.getFeatureClasses();

            if (currValue.getEntropy() == 0 || currValue.getRowsCount() >= this.MIN_ROWS){
//            if (currValue.getEntropy() == 0 || this.visitedColumns.cardinality() > this.MAX_VISITED_COLUMNS){
                String className = this.findClassName(classes);
                Node leaf = new Node();
                leaf.setFeatureValue(className);
                leaf.setLeaf(true);
                Relation relation = new Relation(leaf, valueName);
                node.setClassesProbability(this.getClassesProbability(unvisitedRows));
                node.addRelation(relation);
            } else {
                Node child = new Node();
                Relation relation = new Relation(child, valueName);
                node.setClassesProbability(this.getClassesProbability(unvisitedRows));
                node.addRelation(relation);
                this.build(child, currValue.getRows());
            }
        }
    }

    public String classify(String[] features){
        return classify(features, this.rootNode);
    }

    private String classify(String[] features, Node node){
        if (node.isLeaf()){
            return node.getFeatureValue();
        }

        String value = features[node.getIndex()];
        for (Relation relation: node.getChildren()) {
            if (relation.hasRelationValue(value)){
                return classify(features, relation.getNextNode());
            }
        }

        String bestClass = "";
        double maxProbability = 0;
        Map<String, Double> classesProbability = node.getClassesProbability();
        for (String entryClass: classesProbability.keySet()) {
            if (maxProbability < classesProbability.get(entryClass)) {
                maxProbability = classesProbability.get(entryClass);
                bestClass = entryClass;
            }
        }
        return bestClass;
    }

    private Feature getBestFeature(BitSet unvisitedRows){
        Feature bestFeature = null;
        double bestInformationGain = Double.MIN_VALUE;

        for (int i = 1; i < this.COLUMNS_COUNT; i++){
            if (visitedColumns.get(i)){
                continue;
            }

            Feature feature = this.getFeatureInfo(i, unvisitedRows);
            Map<String, ValueInfo> values = feature.getValues();

            double informationGain = this.calculateInformationGain(values, feature.getRowCount());
            if (bestInformationGain < informationGain){
                bestInformationGain = informationGain;
                bestFeature = feature;
                bestFeature.setIndex(i);
            }

        }

        if (bestFeature != null){
            visitedColumns.set(bestFeature.getIndex());
        }
        return bestFeature;
    }

    private Feature getFeatureInfo(Integer index, BitSet unvisitedRows){
        Feature feature = new Feature(index);
        Map<String, ValueInfo> values = new HashMap<>();
        feature.setFeatureName(DataMapping.FEATURES[index]);
        for (int i = 1; i < this.rowCount; i++){
            if (!unvisitedRows.get(i)){
                continue;
            }

            String className = this.table[i][0];
            feature.setFeatureName(this.table[0][index]);
            String value = this.table[i][index];

            if (!values.containsKey(value)){
                values.put(value, new ValueInfo(new BitSet(this.rowCount)));
            }

            ValueInfo valueInfo = values.get(value);
            valueInfo.increaseClassFrequency(className);
            valueInfo.addRow(i);
            valueInfo.increaseRowCount();
        }

        feature.setRowCount(this.rowCount);
        feature.setValues(values);
        return feature;
    }

    private double calculateTotalEntropy(Map<String, ValueInfo> values, Integer rowCounter){
        double finalEntropy = 0.0;
        for (String value :
                values.keySet()) {
            ValueInfo valueInfo = values.get(value);
            double entropy = calculateEntropy(new ArrayList<Integer>(valueInfo.getFeatureClasses().values()),
                    valueInfo.getRowsCount());
            valueInfo.setEntropy(entropy);
            finalEntropy += ((double)valueInfo.getRowsCount()/rowCounter)*entropy;
        }

        return finalEntropy;
    }

    public double calculateInformationGain(Map<String, ValueInfo> values, Integer allRecords){
        double classesEntropy = this.calculateClassesEntropy(values.values(), allRecords);
        double entropy = this.calculateTotalEntropy(values, allRecords);

        return classesEntropy - entropy;
    }

    private double calculateClassesEntropy(Collection<ValueInfo> values, Integer allRecords){
        double entropy = 0;
        Map<String, Integer> classesFrequency = new HashMap<>();
        for (String className :
                DataMapping.CLASS_NAMES) {
            classesFrequency.put(className, 0);
        }

        for (ValueInfo info :
                values) {
            Map<String, Integer> featureClasses = info.getFeatureClasses();
            for (String valueClass :
                    featureClasses.keySet()) {
                int frequency = classesFrequency.get(valueClass);
                frequency += featureClasses.get(valueClass);
                classesFrequency.replace(valueClass,frequency);
            }
        }

        for (String className :
                classesFrequency.keySet()) {
            double probability = (double)classesFrequency.get(className) / allRecords;
            entropy -=probability*Math.log(probability);
        }

        return entropy;
    }
    
    public double calculateEntropy(List<Integer> classRecords, Integer allRecordsSize) {
        double entropy = 0.0;
        for (int i = 0; i < classRecords.size(); i++){
            double probability = (double) classRecords.get(i)/allRecordsSize;
            if (probability != 0) {
                entropy -= probability*Math.log(probability);
            }
        }

        return entropy;
    }

    private String findClassName(Map<String, Integer> classes){
        int maxFrequency = -1;
        String result = "";
        for (String className :
                classes.keySet()) {
            if (classes.get(className) > maxFrequency) {
                maxFrequency = classes.get(className);
                result = className;
            }
        }

        return result;
    }

    private Map<String, Double> getClassesProbability(BitSet unvisitedRows){
        Map<String, Double> classFrequency = new HashMap<>();
        for (String className :
                DataMapping.CLASS_NAMES) {
            classFrequency.put(className, 0.0);
        }

        unvisitedRows.clear(this.CLASS_COLUMN_INDEX);
        for (int i = 1; i < unvisitedRows.cardinality(); i++){
            String className = this.table[unvisitedRows.nextSetBit(i)][this.CLASS_COLUMN_INDEX];
            double frequency = classFrequency.get(className);
            classFrequency.replace(className, ++frequency);
        }

        for (String className :
                classFrequency.keySet()) {
            double frequency = classFrequency.get(className);
            classFrequency.replace(className, frequency/unvisitedRows.cardinality());
        }
        return classFrequency;
    }

    private String getClassForUnvisitedRows(BitSet unvisitedRows){
        Map<String, Integer> classFrequency = new HashMap<>();
        for (String className :
                DataMapping.CLASS_NAMES) {
            classFrequency.put(className, 0);
        }
        String bestClass = "";
        Integer maxClassFrequency = 0;
        for (int i = 0; i < unvisitedRows.cardinality(); i++){
            String className = this.table[unvisitedRows.nextSetBit(i)][this.CLASS_COLUMN_INDEX];
            int frequency = classFrequency.get(className);
            classFrequency.replace(className, ++frequency);
            if (maxClassFrequency < frequency + 1){
                maxClassFrequency = frequency + 1;
                bestClass = className;
            }
        }

        return bestClass;
    }
}
