package com.gevbagratunyan.school.service.services;




import com.gevbagratunyan.school.entity.data.EmployeeBanking;
import com.gevbagratunyan.school.entity.enums.Position;
import com.gevbagratunyan.school.entity.enums.Specialization;
import com.gevbagratunyan.school.entity.models.Employee;
import com.gevbagratunyan.school.repository.EmployeeRepo;
import com.gevbagratunyan.school.service.crud.CreateSupported;
import com.gevbagratunyan.school.service.crud.DeleteSupported;
import com.gevbagratunyan.school.service.crud.ReadSupported;
import com.gevbagratunyan.school.service.crud.UpdateSupported;
import com.gevbagratunyan.school.transfer.user.request.EmployeeBankingUpdateRequest;
import com.gevbagratunyan.school.transfer.user.request.EmployeeCreateRequest;
import com.gevbagratunyan.school.transfer.user.request.EmployeeUpdateRequest;
import com.gevbagratunyan.school.transfer.employee.EmployeeResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EmployeeService implements CreateSupported<EmployeeCreateRequest,
        EmployeeResponse>, ReadSupported<Long, EmployeeResponse>,
        UpdateSupported<Long, EmployeeUpdateRequest, EmployeeResponse>,
        DeleteSupported<Long> {

    private final EmployeeRepo employeeRepository;

    public EmployeeService(EmployeeRepo employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    @Override
    public EmployeeResponse get(Long id) {
        Employee employee = employeeRepository.findById(id).get();
        EmployeeBanking banking = employee.getEmployeeBanking();
        EmployeeResponse response = new EmployeeResponse();
        BeanUtils.copyProperties(employee, response);
        BeanUtils.copyProperties(banking,response);
        response.setSpecialization(employee.getSpecialization().toString());
        response.setPosition(employee.getPosition().toString());

        return response;
    }

    @Transactional
    @Override
    public EmployeeResponse add(EmployeeCreateRequest employeeCreateRequest) {
        Employee employee = new Employee();
        EmployeeBanking banking = new EmployeeBanking();
        employee.setSpecialization(Specialization.stringToEnum(employeeCreateRequest.getSpecialization()));
        employee.setPosition(Position.stringToEnum(employeeCreateRequest.getPosition()));

        LocalDate birthDate = createBirthDate(employeeCreateRequest.getBirthDate());

        BeanUtils.copyProperties(employeeCreateRequest, employee);
        BeanUtils.copyProperties(employeeCreateRequest,banking);
        employee.setEmployeeBanking(banking);
        employee.setBirthDate(birthDate);
        employee.setCreatedDate(new Date(System.currentTimeMillis()));
        Employee savedEmployee =  employeeRepository.save(employee);
        EmployeeResponse response = new EmployeeResponse();
        BeanUtils.copyProperties(savedEmployee,response);
        response.setPosition(savedEmployee.getPosition().toString());
        response.setSpecialization(savedEmployee.getSpecialization().toString());
        response.setBankCard(savedEmployee.getEmployeeBanking().getBankCard());
        response.setSalary(employee.getEmployeeBanking().getSalary());
        return response;
    }

    @Transactional
    @Override
    public EmployeeResponse update(Long id, EmployeeUpdateRequest updateRequest) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee not found"));

        if(updateRequest.getSpecialization()!=null){
            employee.setSpecialization(Specialization.stringToEnum(updateRequest.getSpecialization()));
        }

        if(updateRequest.getPosition()!=null){
            employee.setPosition(Position.stringToEnum(updateRequest.getPosition()));
        }

        if(updateRequest.getMail()!=null){
            employee.setMail(updateRequest.getMail());
        }

        employee.setUpdatedDate(new Date(System.currentTimeMillis()));

        Employee savedEmployee =  employeeRepository.save(employee);
        EmployeeResponse response = new EmployeeResponse();
        BeanUtils.copyProperties(savedEmployee,response);
        response.setPosition(savedEmployee.getPosition().toString());
        response.setSpecialization(savedEmployee.getSpecialization().toString());
        return response;
    }

    @Override
    public void delete(Long id) {
        boolean exists = employeeRepository.existsById(id);
        if (!exists) {
            throw new NoSuchElementException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }

    @Transactional
    public EmployeeResponse updateBanking(Long id, EmployeeBankingUpdateRequest updateRequest){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee not found"));
        EmployeeBanking banking = employee.getEmployeeBanking();

        if(updateRequest.getSalary()!=0){
            banking.setSalary(updateRequest.getSalary());
        }

        if(updateRequest.getBankCard()!=null){
            banking.setBankCard(updateRequest.getBankCard());
        }

        if(updateRequest.getBalance()!=0){
            banking.setBalance(updateRequest.getBalance());
        }

        employee.setUpdatedDate(new Date(System.currentTimeMillis()));

        Employee savedEmployee = employeeRepository.save(employee);

        EmployeeResponse response = new EmployeeResponse();
        BeanUtils.copyProperties(savedEmployee,response);
        BeanUtils.copyProperties(savedEmployee.getEmployeeBanking(),response);
        response.setPosition(savedEmployee.getPosition().toString());
        response.setSpecialization(savedEmployee.getSpecialization().toString());
        return response;
    }

    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }

    public int getVacationDays(Long id){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(()-> new UsernameNotFoundException("Employee not found"));
        Period diff = Period.between(employee.getEmployeeBanking().getVacationStartDate(), employee.getEmployeeBanking().getVacationEndDate());
        int vacationDays = diff.getDays();
        return vacationDays;
    }

    public void setVacation(Long id, boolean inVacation){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(()-> new UsernameNotFoundException("Employee not found"));
        employee.getEmployeeBanking().setInVacation(inVacation);
        employeeRepository.save(employee);
    }

    private LocalDate createBirthDate(String birthDate){

        String[] bd = birthDate.split(",");
        int[] bdt = new int[3];
        for(int i=0;i<3;i++){
            bdt[i] = Integer.parseInt(bd[i]);
        }
        return LocalDate.of(bdt[0], bdt[1], bdt[2]);
    }

}
