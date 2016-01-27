package kz.hts.ce.service;

import kz.hts.ce.model.entity.Check;
import kz.hts.ce.model.entity.Employee;
import kz.hts.ce.repository.CheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CheckService extends BaseService<Check, CheckRepository> {
    @Autowired
    protected CheckService(CheckRepository repository) {
        super(repository);
    }

    public List<Check> findByCheckNumber(String checkNumber){
        return repository.findByCheckNumber(checkNumber);
    }

    public List<Check> findByEmployeeUsername(String userName){
        return repository.findByEmployee_Username(userName);
    }

    public List<Check> findByEmployeeId(UUID uuid){
        return repository.findByEmployee_Id(uuid);
    }

    public List<Check> findByDateBetweenAndEmployeeId(Date start, Date end, UUID uuid){
        return repository.findByDatesBetweenAndEmployeeId(start, end, uuid);
    }

    public Check findByLastDate(){
        return repository.findByLastDate();
    }

    public List<Check> findByDateBetween(Date startDate, Date endDate){
        return repository.findByDateBetween(startDate, endDate);
    }
}
