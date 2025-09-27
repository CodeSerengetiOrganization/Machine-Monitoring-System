package com.mytech.machinemonitorsystem.service;

import com.mytech.machinemonitorsystem.dto.FailedProductDto;
import com.mytech.machinemonitorsystem.entity.FailedProductCumulative;
import com.mytech.machinemonitorsystem.entity.FalseAlarmMachineSummary;
import com.mytech.machinemonitorsystem.repository.FalseAlarmRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FalseAlarmService {
    @Autowired
    FalseAlarmRepository falseAlarmRepository;

    /**
     * Retrieves a list of false alarm summaries(contains the failed part data) for a specific machine, rack, and channel.
     * This method is transactional to ensure consistency during the data retrieval process.
     *
     * @param machineCode   the unique code identifying the machine
     * @param rackCode      the code identifying the rack which is compatible with the machine
     * @param channelNumber the number identifying the specific channel of the rack to query
     * @return a list of {@link FalseAlarmMachineSummary} objects representing the false alarms
     */
    @Transactional
    public List<FailedProductDto> getFalseAlarmsForMachine(Integer machineCode,Integer rackCode,Integer channelNumber){
        List<Integer> availableRackCodeList = new ArrayList<>();
        List<Integer> availableChannelList = new ArrayList<>();
        Set<List<Integer>> combinations =  new HashSet<List<Integer>>();
        int mockedBatchSize = 200;  //temperily harde code the mocked data
        //1. pre-check of input parameters
        if(machineCode !=null && machineCode < 0){
            throw new IllegalArgumentException("machineCode should be greater than or equal to 0. Current machineCode:"+machineCode);
        }
        if(rackCode !=null && rackCode < 0){
            throw new IllegalArgumentException("rackCode should be greater than or equal to 0. Current rackCode:"+rackCode);
        }
        if(channelNumber!=null && channelNumber < 0){
            throw new IllegalArgumentException("channelNumber should be greater than or equal to 0. Current channelNumber:"+channelNumber);
        }
        if(machineCode == null || machineCode.equals(0)){
            //need a default machine id, should use all compatible rackCode, should use all available channel numbers
            machineCode = 4;
        }

        if(rackCode == null || rackCode.equals(0)){
            //use all available racks
            availableRackCodeList = getAvailableRackCodeList(machineCode);
        }else{
            //use current existing rack code
            availableRackCodeList.add(rackCode);
        }
        System.out.println("availableRackCodeList:"+availableRackCodeList.toString());

        if(channelNumber == null || channelNumber.equals(0)){
            availableChannelList = getAvailableChannelList(rackCode);
        }else{
            availableChannelList.add(channelNumber);
        }
        System.out.println("availableChannelList:"+availableChannelList.toString());

        //build the combination set
        for(Integer rack:availableRackCodeList){
            for (Integer channel: availableChannelList){
                combinations.add(List.of(machineCode,rack,channel));
            }
        }
        //for debugging, print the combination
        System.out.println("build the combinations:" +combinations.toString());
//        return null;

        List<FailedProductDto> FailedProductDtos = new ArrayList<>();
        falseAlarmRepository.triggerStoredProcedure();
        for(List<Integer>  combination : combinations){
            System.out.println("combination:");
            for(int i=0;i<combination.size();i++){
                System.out.println(combination.get(i));
            }

            List<FalseAlarmMachineSummary> falseAlarmDataList = falseAlarmRepository.findByMachineParameters(combination.get(0),combination.get(1),combination.get(2));
            System.out.println("falseAlarmDataList in loop:"+falseAlarmDataList.toString());
            //construct dto based on retrieved data,put into the list to return
            List<Integer> failedProductCountList =new ArrayList<>();
            for(FalseAlarmMachineSummary summary:falseAlarmDataList){
                failedProductCountList.add(summary.getFalseAlarmCount());
            }
            FailedProductDto dto =new FailedProductDto();
            dto.setMachineId(combination.get(0));
            dto.setRackId(combination.get(1));
            dto.setChannelNumber(combination.get(2));
            dto.setBatchSize(mockedBatchSize);

            dto.setFailedProductCount(failedProductCountList);
            FailedProductDtos.add(dto);
//            falseAlarms.addAll(falseAlarmDataList);
        }
//        System.out.println("falseAlarms:"+falseAlarms.toString());
        System.out.println("FailedProductDtos:"+FailedProductDtos.toString());
        return FailedProductDtos;
    }

    /**
     * Get all available channel number that is on the rack
     * @param rackCode rack Id
     * @return the available channel numbers that are on the provided machineCode
     * */
    List<Integer> getAvailableChannelList(Integer rackCode) {
        return (List.of(1,2)); //mocked data
    }

    /**
    * Get all available rack that is compatible with provided
    * @param machineCode machine Id
     * @return the available rack code that are compatible with provided machineCode
     * */
    List<Integer> getAvailableRackCodeList(Integer machineCode) {
        //mocked logic
        List<Integer> availableRacks = new ArrayList<>();
        if(machineCode == null || machineCode.equals(4)){
            availableRacks.add(104);
        }else{
            availableRacks.add(105);
        }
        return availableRacks;
    }

    //create comment for this method
    /*
    * */

    private int getLatestProductSequence(){
        //mocked logic
        return 4800;
    }
    List<Long> calculateFailedProductCountInBatch(long latestSeq, int batchSize, int range, List<FailedProductCumulative> failedCumulativeList){
        if (failedCumulativeList == null || failedCumulativeList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> resultArray = new ArrayList<>();

        // Sort descending by productSequence
        List<FailedProductCumulative> sortedList = failedCumulativeList.stream()
                .sorted(Comparator.comparing(FailedProductCumulative::getProductSequence).reversed())
                .collect(Collectors.toList());

        //create 2 helper list
        List<Long> seqList =new ArrayList<>();
        List<Long> cumuList =new ArrayList<>();
        for(FailedProductCumulative item:sortedList){
            seqList.add(item.getProductSequence());
            cumuList.add(item.getCumulativeFailCount());
        }
        System.out.println("seqList:"+seqList.toString());
        System.out.println("cumuList:"+cumuList.toString());

        int batchCounter =1;
        // need 2 parameter for batch window
        long batchStartSeq = latestSeq;
        long batchEndSeq = latestSeq-batchSize+1 >=0 ? latestSeq-batchSize+1:0;


        //need 2 index for seqList
        int seqStartIndex = 0;
        int seqEndIndex = 0;
        int seqIndex = 0;

        //need a resultlist
        List<Long> resultList =new ArrayList<>();

        while(seqIndex < seqList.size()){

            //no fialed product in the batch
            if(seqList.get(seqIndex) < batchEndSeq){
                resultList.add(0L);
                System.out.println(String.format("No failed part in this batch.batchStartSeq: %d,batchEndSeq: %d",batchStartSeq,batchEndSeq));
//                seqIndex++;
                batchCounter++;
                batchStartSeq = batchEndSeq-1;
//                batchEndSeq = latestSeq-batchSize*batchCounter >=0 ? latestSeq-batchSize*batchCounter+1:0;
                batchEndSeq = batchEndSeq-batchSize >=0 ? batchEndSeq-batchSize:0;


                continue;
            }
            if(seqList.get(seqIndex) > latestSeq){
                throw new IllegalArgumentException(String.format("The Sequence in the list is larger than current latest Sequence, it is not normal.Current sequence: %d, StartSeq: %d",seqList.get(seqIndex),batchStartSeq));
            }
            //find the start index
            if(seqList.get(seqIndex)< batchStartSeq &&seqList.get(seqIndex)>batchEndSeq){
                seqStartIndex=seqIndex;
            }
            //find the end index
            while(seqIndex< seqList.size() && seqList.get(seqIndex)>= batchEndSeq) {
                seqIndex++;
            }
//            seqIndex-=1;
            seqEndIndex=seqIndex-1;

            long failedCount = cumuList.get(seqStartIndex) - cumuList.get(seqEndIndex)+1;
            resultList.add(failedCount);
            System.out.println("seqStartIndex:"+seqStartIndex+";seqEndIndex:"+seqEndIndex);
            System.out.println("batchStartSeq:"+batchStartSeq+";batchEndSeq:"+batchEndSeq);

            System.out.println("failedCount:"+failedCount);
            //move to next batch
            batchCounter++;
            batchStartSeq = latestSeq - batchSize*(batchCounter-1);
            batchEndSeq = latestSeq - batchSize*batchCounter+1 >=0 ? latestSeq - batchSize*batchCounter+1:0;
//            seqIndex++;
        }


        System.out.println("resultList:"+resultList.toString());
        return resultList;
    }

}
