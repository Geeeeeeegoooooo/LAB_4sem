package lab1.demo.controller;

import lab1.demo.service.RequestCounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
@Service
@RestController
@RequestMapping("/counter")
public class RequestCounterController {

    @Autowired
    private RequestCounterService requestCounterService;

    @GetMapping
    public int getRequestCount() {
        return requestCounterService.getCount();
    }
}
