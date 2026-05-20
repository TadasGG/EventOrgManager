package lt.bropro.inventorymanager.server.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lt.bropro.inventorymanager.server.database.EmployeeRepository;
import lt.bropro.inventorymanager.server.exceptions.EmployeeNotFoundException;
import lt.bropro.inventorymanager.server.model.Employee;
import lt.bropro.inventorymanager.server.model.EmployeeChange;
import lt.bropro.inventorymanager.server.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/employees/me")
@Tag(name = "Employee Self Controller")
public class EmployeeSelfController {
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public Employee me() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        return employeeRepository.findByEmail(email).orElseThrow();
    }

    @PutMapping()
    public ResponseEntity<Employee> updateEmployee(@RequestBody Employee employeeDetails) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            employee.setFirstName(employeeDetails.getFirstName());
            employee.setLastName(employeeDetails.getLastName());
            employee.setAddress1(employeeDetails.getAddress1());
            employee.setAddress2(employeeDetails.getAddress2());
            employee.setEmail(employeeDetails.getEmail());
            employee.setMobilePhone(employeeDetails.getMobilePhone());

            Employee updatedEmployee = employeeRepository.save(employee);
            return ResponseEntity.ok(updatedEmployee);
        } else {
            throw new EmployeeNotFoundException(null);
        }
    }

    @PutMapping("/password")
    public ResponseEntity<Employee> updateEmployeePassword(@RequestBody EmployeeChange employeeDetails) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();

            if (passwordEncoder.matches(employeeDetails.getCurrentPassword(), employee.getPassword())){
                if (employeeDetails.getNewPassword().equals(employeeDetails.getConfirmPassword())){
                    String hashedPassword = passwordEncoder.encode(employeeDetails.getNewPassword());
                    employee.setPassword(hashedPassword);

                    Employee updatedEmployee = employeeRepository.save(employee);
                    return ResponseEntity.ok(updatedEmployee);
                } else {
                    throw new RuntimeException("2");
                }
            } else {
                throw new RuntimeException("1");
            }
        } else {
            throw new EmployeeNotFoundException(null);
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getEmployeesRolesByEmail() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);
        if (optionalEmployee.isPresent()) {
            List<Role> employeeRoles = optionalEmployee.get().getRoleList();
            return ResponseEntity.ok().body(employeeRoles);
        } else {
            throw new EmployeeNotFoundException(null);
        }
    }
}
