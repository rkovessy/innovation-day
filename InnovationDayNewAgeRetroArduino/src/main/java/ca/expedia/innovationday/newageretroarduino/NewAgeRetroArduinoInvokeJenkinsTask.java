package ca.expedia.innovationday.newageretroarduino;

import org.springframework.beans.factory.annotation.Autowired;

import ca.expedia.innovationday.newageretroarduino.service.BrushTeethToBlindService;


public class NewAgeRetroArduinoInvokeJenkinsTask {
 
    @Autowired
    private BrushTeethToBlindService service;
    
    public void executeBrushTeeth() {
        service.brushTeeth();
    } 
}
