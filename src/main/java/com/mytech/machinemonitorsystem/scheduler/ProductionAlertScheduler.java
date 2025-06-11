package com.mytech.machinemonitorsystem.scheduler;

import com.mytech.machinemonitorsystem.dto.FailedProductDto;
import com.mytech.machinemonitorsystem.entity.MachineUnit;
import com.mytech.machinemonitorsystem.service.EmailService;
import com.mytech.machinemonitorsystem.service.FalseAlarmService;
import com.mytech.machinemonitorsystem.service.MachineLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class ProductionAlertScheduler {

    @Autowired
    EmailService emailService;
    @Autowired
    FalseAlarmService falseAlarmService;
    @Autowired
    MachineLineService machineLineService;

    @Value("${app.alert.recipients}")
    private String[] defaultRecipients;

    @Scheduled(fixedRate = 60*1000)
    public void monitorFailedProduction(){
        //0.0 test
        System.out.println("Scheduled method monitorFailedProduction is working, now:"+ LocalDateTime.now());
        Set<Integer> machineCodeInMailTitle = new HashSet<>();
        //0.define email list
        List<MachineUnit> machineUnitsForAlert =  new ArrayList<>();
        Map<String, Integer> failedProductMap = new HashMap<>();
        //1. get failed production count for each combination of machineId+RackId+channelNumber use the count of last batch
        Set<MachineUnit> machineRackChannelCombinations = machineLineService.getMachineRackChannelCombinations();
        //2. check each combination, any one who reach the criteria, put into alertEmailMap
        Iterator<MachineUnit> it = machineRackChannelCombinations.iterator();
        while(it.hasNext()){
            MachineUnit machineUnit = it.next();
            List<FailedProductDto> failedProductDtosForMachineUnit = falseAlarmService.getFalseAlarmsForMachine(machineUnit.getMachineId(), machineUnit.getRackId(), machineUnit.getChannelNumber());
            for(FailedProductDto dto:failedProductDtosForMachineUnit){
                List<Integer> failedProductCounts = dto.getFailedProductCount();
                for(Integer count:failedProductCounts){
                    if(count >=7){
                        //add this machine unit into email list
                        machineUnitsForAlert.add(machineUnit);
                        //add this machine unit into the list to shown in email title
                        machineCodeInMailTitle.add(machineUnit.getMachineId());
                        //todo: do something that can help to build the machineUnitListStr
                        String key = ""+machineUnit.getMachineId()+"-"+machineUnit.getRackId()+"-"+machineUnit.getChannelNumber();
                        failedProductMap.put(key,count);
                    }
                }
            }
        }
        //3. based on the machineUnit list, construct email content and send email
        String[] to = defaultRecipients;
        String subject = "Alert for Machine "+ String.join(",",machineCodeInMailTitle.stream()
                                                                                            .map(String::valueOf)
                                                                                            .toList());//machineCodeInTitle.toString();
        String templateName = "emails/alert-template";
        Context context = new Context();
//        String machineUnitListStr =String.format("Machine unit: %s, failed product amount in batch: %d units.",machineCode,7);
        String machineUnitListStr = buildContentWithMachineUnitAndFailedCount(failedProductMap);
        context.setVariable("machineUnitListStr",machineUnitListStr);
        context.setVariable("alertTimestamp",LocalDateTime.now());

        try {
            emailService.sendTemplatedHtmlEmail(to,subject,templateName,context);
        } catch (Exception e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
        }

    }

    private String buildContentWithMachineUnitAndFailedCount(Map<String, Integer> failedProductMap) {
//        String content = "";
        StringBuilder content = new StringBuilder();
        if(failedProductMap != null && !failedProductMap.isEmpty()){
            failedProductMap.forEach((machineUnit,failedCount)->{
                content.append(String.format("Machine unit: %s, failed product amount in batch: %d units.<br>",machineUnit,failedCount));
            });
        }
//        content += String.format("Machine unit: %s, failed product amount in batch: %d units.",machineCode,7);
        return content.toString();
    }


}
