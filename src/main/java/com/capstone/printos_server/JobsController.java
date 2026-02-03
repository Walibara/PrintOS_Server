@RestController
@RequestMapping("/api")
public class JobController {

    @Autowired
    private JobService service;

    @GetMapping("/jobs")
    public List<Job> getJobs() {
        return service.getAllJobs();
    }
}