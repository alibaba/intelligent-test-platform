package com.alibaba.markovdemo.engine.AI.GACaseGenerator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class GAOperator {


    public double fiteness(String[] caseCov, Map<String, Integer> historyCov){
        int k = 1; //累加常量因子
        int u = 100; //逆频率放大因子

        double f = 0.0; //适应度结果

        //计算用例适应度
        for(String covLine : caseCov){
            f += (k + u*(1.0d/historyCov.get(covLine)));
        }

        return f;
    }





    public List<Object[]> selection(List<Object[]> population, List<Double> fitnessValue){
        //轮盘赌选择法：适应度越高的用例被选中的概率越大
        Double[] fitnessSum = new Double[fitnessValue.size()];
        for(int i = 0; i < fitnessValue.size(); i++){
            if(i==0){
                fitnessSum[i] = fitnessValue.get(i);
            }else{
                fitnessSum[i] = fitnessSum[i-1] + fitnessValue.get(i);
            }
        }

        Double totalSum = fitnessSum[fitnessSum.length-1];
        for(int i = 0; i < fitnessValue.size(); i++){
            fitnessSum[i] /= totalSum;
        }

        //选择种群个体
        List<Object[]> newPopulation = new ArrayList<>();
        for(int i = 0; i < fitnessValue.size(); i++){
            Double rand = new Random().nextDouble();
            for(int j = 0; j < fitnessValue.size(); j++){
                if( j==0 ){
                    if( rand < fitnessSum[j]){
                        newPopulation.add(population.get(j));
                    }
                }else{
                    if( fitnessSum[j-1] < rand && fitnessSum[j] >= rand ){
                        newPopulation.add(population.get(j));
                    }
                }
            }
        }
        return newPopulation;
    }




    public List<Object[]> crossover(List<Object[]> newPopulation){
        Double pc = 0.8; //交叉概率
        int halfNum = newPopulation.size()/2;

        Random rand = new Random();

        List<Object[]> fathers = newPopulation.subList(0,halfNum);
        List<Object[]> mothers = newPopulation.subList(halfNum,newPopulation.size());

        shuffle(fathers);
        shuffle(mothers);

        List<Object[]> nextPopulation = new ArrayList<>();
        for(int i=0; i<halfNum; i++){
            Object[] son = new Object[fathers.get(i).length];
            Object[] daughter = new Object[fathers.get(i).length];
            if(rand.nextDouble() <= pc){
                int crossPoint = rand.nextInt((int)fathers.get(i).length);
                for(int m = 0; m<crossPoint; m++){
                    son[m] = fathers.get(i)[m];
                    daughter[m] = mothers.get(i)[m];
                }
                for(int n = crossPoint; n<fathers.get(i).length; n++){
                    son[n] = mothers.get(i)[n];
                    daughter[n] = fathers.get(i)[n];
                }

            }else{
                son = fathers.get(i);
                daughter = mothers.get(i);
            }
            nextPopulation.add(son);
            nextPopulation.add(daughter);
        }

        return nextPopulation;
    }




    public List<Object[]> mutation(List<Object[]> population, Map<String, List<Object>> geneBankMap, List<String> chromo){
        double pm = 0.2; //变异概率
        Random rand = new Random();

        for(int i=0; i<population.size(); i++){
            if(rand.nextDouble() <= pm){
                int mutatePosition = rand.nextInt(population.get(i).length);

                String geneKey = chromo.get(mutatePosition);
                List<Object> geneValues = geneBankMap.get(geneKey);

                int mutateValueIndex = rand.nextInt(geneValues.size());

                if(geneValues.get(mutateValueIndex)==null){
                    continue;
                }
                if(geneValues.get(mutateValueIndex).equals(population.get(i)[mutatePosition])){
                    population.get(i)[mutatePosition] = geneValues.get(mutateValueIndex);
                }
            }
        }
        return population;
    }


    public <T> void shuffle(List<T> list) {
        int size = list.size();
        Random random = new Random();

        for(int i = 0; i < size; i++) {
            // 获取随机位置
            int randomPos = random.nextInt(size);

            // 当前元素与随机元素交换
            T temp = list.get(i);
            list.set(i, list.get(randomPos));
            list.set(randomPos, temp);
        }
    }
}
