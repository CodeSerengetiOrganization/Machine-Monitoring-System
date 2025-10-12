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

    @Autowired
    FailedProductService failedProductService;

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
    public List<FailedProductDto> getFalseAlarmsForMachine(Long machineCode,Long rackCode,Long channelNumber){
        List<Long> availableRackCodeList = new ArrayList<>();
        List<Long> availableChannelList = new ArrayList<>();
        Set<List<Long>> combinations =  new HashSet<List<Long>>();
        int mockedBatchSize = 200;  //temperily harde code the mocked data
        //1. pre-check of input parameters
        checkParameter(machineCode,rackCode,channelNumber);
        //2.1 get a default machine id if not provided
        long effectiveMachineCode = machineCode == null || machineCode.equals(0L) ? 4L : machineCode;
//        if(machineCode == null || machineCode.equals(0L)){
//            //need a default machine id, should use all compatible rackCode, should use all available channel numbers
//            machineCode = 4L;
//        }
        //2.2 get all compatible rackCode,
        if(rackCode == null || rackCode.equals(0L)){
            //use all available racks
            availableRackCodeList = getAvailableRackCodeList(machineCode);
        }else{
            //use current existing rack code
            availableRackCodeList.add(rackCode);
        }
//        System.out.println("availableRackCodeList:"+availableRackCodeList.toString());

        //2.3 get all available channel numbers
        if(channelNumber == null || channelNumber.equals(0L)){
            availableChannelList = getAvailableChannelList(rackCode);
        }else{
            availableChannelList.add(channelNumber);
        }
//        System.out.println("availableChannelList:"+availableChannelList.toString());

        //build the combination set
        combinations=buildMachineCombinations(effectiveMachineCode,availableRackCodeList,availableChannelList);

        //for debugging, print the combination
        System.out.println("build the combinations:" +combinations.toString());
//        return null;

        List<FailedProductDto> FailedProductDtos = new ArrayList<>();

//        falseAlarmRepository.triggerStoredProcedure();
        for(List<Long>  combination : combinations){
            System.out.println("combination:");
            for(int i=0;i<combination.size();i++){
                System.out.println(combination.get(i));
            }

            long latestProductSequence = getLatestProductSequence();
            //todo: remove the hard coded data
            int mockedAnalyzeRange = 10; //temporarily hard code the mocked data
            //todo: should use effectiveMachineCode in future,not hard code data
            List<FailedProductCumulative> failedCumulativeList = failedProductService.findByProductCodeAndStationCodeAndStationChannelNumber(112233L,combination.get(0),combination.get(2));
            List<Long> failedProductCountList = calculateFailedProductCountInBatch(latestProductSequence, mockedBatchSize, mockedAnalyzeRange, failedCumulativeList);
            FailedProductDto dto =new FailedProductDto();
            dto.setMachineId(combination.get(0));
            dto.setRackId(combination.get(1));
            dto.setChannelNumber(combination.get(2));
            dto.setBatchSize(mockedBatchSize);

            dto.setFailedProductCount(failedProductCountList);
            FailedProductDtos.add(dto);
        }
//        System.out.println("FailedProductDtos:"+FailedProductDtos.toString());
        return FailedProductDtos;
    }

    /*
    * This method build the Machine+Rack+ChannelNumber combination.
    *  @param effectiveMachineCode   the unique code identifying the machine
     * @param rackCode      the code identifying the rack which is compatible with the machine
     * @param channelNumber the number identifying the specific channel of the rack to query

     * */
    private Set<List<Long>> buildMachineCombinations(long effectiveMachineCode, List<Long> availableRackCodeList, List<Long> availableChannelList) {
        Set<List<Long>> combinations = new HashSet<List<Long>>();
        for(Long rack:availableRackCodeList){
            for (Long channel: availableChannelList){
                combinations.add(List.of(effectiveMachineCode,rack,channel));
            }
        }
        return combinations;
    }


    /*
    * This method is to check the eligibility of parameters.
     * @param machineCode   the unique code identifying the machine
     * @param rackCode      the code identifying the rack which is compatible with the machine
     * @param channelNumber the number identifying the specific channel of the rack to query
     * */

    private void checkParameter(Long machineCode,Long rackCode,Long channelNumber) {
        if(machineCode !=null && machineCode < 0L){
            throw new IllegalArgumentException("machineCode should be greater than or equal to 0. Current machineCode:"+machineCode);
        }
        if(rackCode !=null && rackCode < 0L){
            throw new IllegalArgumentException("rackCode should be greater than or equal to 0. Current rackCode:"+rackCode);
        }
        if(channelNumber!=null && channelNumber < 0L){
            throw new IllegalArgumentException("channelNumber should be greater than or equal to 0. Current channelNumber:"+channelNumber);
        }
    }

    /**
     * Get all available channel number that is on the rack
     * @param rackCode rack Id
     * @return the available channel numbers that are on the provided machineCode
     * */
    List<Long> getAvailableChannelList(Long rackCode) {
        return (List.of(1L,2L)); //mocked data
    }

    /**
    * Get all available rack that is compatible with provided
    * @param machineCode machine Id
     * @return the available rack code that are compatible with provided machineCode
     * */
    List<Long> getAvailableRackCodeList(Long machineCode) {
        //mocked logic
        List<Long> availableRacks = new ArrayList<>();
        if(machineCode == null || machineCode.equals(4L)){
            availableRacks.add(104L);
        }else{
            availableRacks.add(105L);
        }
        return availableRacks;
    }

    //create comment for this method
    /*
    * */

    private long getLatestProductSequence(){
        //mocked logic
//        return 4800L; //use this mocked data whin channelNumber=1
        return 7200L;   //use this mocked data whin channelNumber=2

    }
    /*
    * This method is to calculate the failed product count in each batch
    * @param latestSeq the latest product sequence number
    * @param batchSize the size of each batch
    * @param analyzeRange the analyzeRange of how many batches to calculate(it should be multiple of batchSize)
    * @param failedCumulativeList the list of FailedProductCumulative objects containing product sequence and cumulative fail count
    * @return a list of Long values representing the failed product count in each batch
    * */
    List<Long> calculateFailedProductCountInBatch(long latestSeq, int batchSize, int analyzeRange, List<FailedProductCumulative> failedCumulativeList){
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

        while(seqIndex < seqList.size() && batchCounter<= analyzeRange){

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
