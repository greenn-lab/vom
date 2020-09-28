package vom.server.api.sample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sample")
public class SampleApi {
  
  @GetMapping
  public String echo(String echo) {
    return echo;
  }
  
}
