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

    public void setDefaultRecipients(String[] recipients){
        this.defaultRecipients = recipients;
    }

//    @Scheduled(fixedRate = 60*1000)
    @Scheduled(fixedRateString = "${app.scheduling.monitorFailedProduct.fixedRate}")
    public void monitorFailedProduction(){
        Set<Integer> machineCodeInMailTitle = new HashSet<>();
        //0.define email list
        List<MachineUnit> machineUnitsForAlert =  new ArrayList<>();
        Map<String, Integer> failedProductMap = new HashMap<>();
        //1. get failed production count for each combination of machineId+RackId+channelNumber use the count of last batch
        Set<MachineUnit> machineRackChannelCombinations = machineLineService.getMachineRackChannelCombinations();
        //2. check each combination, any one who reach the criteria, put into alertEmailMap
        if (machineRackChannelCombinations == null || machineRackChannelCombinations.isEmpty()){
            //todo: log and exist method
            System.out.println("There is no available combination of machine+rack+channel number. Logging time:"+LocalDateTime.now());
            return;
        }
        machineRackChannelCombinations.stream()
                .filter(Objects::nonNull)
                .forEach(machineUnit->{
                    List<FailedProductDto> failedProductDtosForMachineUnit = falseAlarmService.getFalseAlarmsForMachine(machineUnit.getMachineId(), machineUnit.getRackId(), machineUnit.getChannelNumber());
                    if(failedProductDtosForMachineUnit !=null){
                        failedProductDtosForMachineUnit.stream()
                                .filter(Objects::nonNull)
                                .forEach(dto->{
                                    List<Integer> failedProductCounts = dto.getFailedProductCount();
                                    if(failedProductCounts !=null){
                                        failedProductCounts.stream()
                                                .filter(count->count>=7)
                                                .findFirst()
                                                .ifPresent(triggeringCount->{
                                                    //add this machine unit into email list
                                                    machineUnitsForAlert.add(machineUnit);
                                                    //add this machine unit into the list to shown in email title
                                                    machineCodeInMailTitle.add(machineUnit.getMachineId());
                                                    //add to failedProductMap which can help to build the machineUnitListStr
                                                    String key = ""+machineUnit.getMachineId()+"-"+machineUnit.getRackId()+"-"+machineUnit.getChannelNumber();
                                                    failedProductMap.put(key,triggeringCount);
                                                });
                                    }
                                });
                    }
                });

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

        //no try-catch is needed as the service layer has handled all exceptions
        emailService.sendTemplatedHtmlEmail(to,subject,templateName,context);
    }

    private String buildContentWithMachineUnitAndFailedCount(Map<String, Integer> failedProductMap) {
//        String content = "";
        StringBuilder content = new StringBuilder();
        if(failedProductMap != null && !failedProductMap.isEmpty()){
            failedProductMap.forEach((machineUnit,failedCount)->{
                content.append(String.format("Machine unit: %s, failed product amount in batch: %d units.<br>",machineUnit,failedCount));
            });
        }
        return content.toString();
    }
}
