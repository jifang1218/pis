/**
 * 
 */
package com.pms.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pms.constants.PMSEntityConstants;
import com.pms.constants.PMSFileType;
import com.pms.entities.PMSFile;
import com.pms.entities.PMSLoginInfo;
import com.pms.entities.PMSProject;
import com.pms.entities.PMSTask;
import com.pms.entities.PMSUser;
import com.pms.services.PMSEntityProvider;
import com.pms.services.PMSSecurityService;
import com.pms.utils.PMSRandom;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jifang
 *
 */
@RestController
@RequestMapping(value="/api/v1/actions", 
            produces="application/json", 
            consumes="application/json")
@Slf4j
public class PMSActionsController {
    @Autowired
    private PMSEntityProvider entityProvider;
    
    @Autowired
    private PMSSecurityService securityService;
    
    @PostMapping(value="/login")
    public ResponseEntity<String> login(@RequestBody @Validated PMSLoginInfo loginInfo) {
    	return new ResponseEntity<>(securityService.login(loginInfo), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('viewer')")
    @GetMapping(value="/logout")
    public ResponseEntity<Void> logout() {
    	log.debug("logout");
    	securityService.logout();
    	
    	return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    
    @PostMapping(value="/register")
    public ResponseEntity<PMSUser> createUser(@RequestBody @Validated PMSUser user, 
    		@RequestParam(name="company_id", required=true) Long companyId) {
    	String password = PMSRandom.randomPassword(PMSEntityConstants.kMaxUserPasswordLen);
    	user.setPassword(password);
        return new ResponseEntity<>(entityProvider.createUser(user, companyId), HttpStatus.CREATED);
    }
    
    // project dependencies
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @GetMapping(value="/depend/projects/{project_id}")
    public List<PMSProject> getDependentProjects(@PathVariable("project_id") Long projectId) {
        return entityProvider.getDependentProjectsById(projectId);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @PostMapping(value="/depend/projects/{project_id}")
    public PMSProject addDependentProjects(@PathVariable("project_id") Long projectId, 
            @RequestBody @Validated List<Long> dependentProjectIds) {
        return entityProvider.addDependentProjectIds(projectId, dependentProjectIds);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @PutMapping(value="/depend/projects/{project_id}")
    public PMSProject setDependentProjects(@PathVariable("project_id") Long projectId, 
            @RequestBody @Validated List<Long> dependentProjectIds) {
        return entityProvider.setDependentProjectIds(projectId, dependentProjectIds);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @DeleteMapping(value="/depend/projects/{project_id}")
    public PMSProject removeDependentProjects(@PathVariable("project_id") Long projectId, 
    		@RequestParam("dependent_project_id") Long dependentProjectId) {
    	List<Long> dependentProjectIds = new ArrayList<>();
    	dependentProjectIds.add(dependentProjectId);
        return entityProvider.removeDependentProjectIds(projectId, dependentProjectIds);
    }
    
    // task dependencies
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @PostMapping(value="/depend/tasks/{task_id}")
    public PMSTask addDependentTasks(@PathVariable("task_id") Long taskId, 
            @RequestBody @Validated List<Long> dependentTaskIds) {
        return entityProvider.addDependentTasks(taskId, dependentTaskIds);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @PutMapping(value="/depend/tasks/{task_id}")
    public PMSTask setDependentTasks(@PathVariable("task_id") Long taskId, 
            @RequestBody @Validated List<Long> dependentTaskIds) {
        return entityProvider.setDependentTasks(taskId, dependentTaskIds);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @DeleteMapping(value="/depend/tasks/{task_id}")
    public PMSTask removeDependentTasks(@PathVariable("task_id") Long taskId, 
    		@RequestParam("dependent_task_id") Long dependentTaskId) {
    	List<Long> dependentTaskIds = new ArrayList<>();
    	dependentTaskIds.add(dependentTaskId);
        return entityProvider.removeDependentTasks(taskId, dependentTaskIds);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @GetMapping(value="/depend/tasks/{task_id}")
    public List<PMSTask> getDependentTasks(@PathVariable("task_id") Long taskId) {
        return entityProvider.getDependentTasks(taskId);
    }
    
    // assign users to task
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @PostMapping(value="/assign/tasks/{task_id}")
    public PMSTask addUsersToTask(@PathVariable("task_id") Long taskId, 
                @RequestBody @Validated List<Long> userIds) {
        return entityProvider.addUsersToTask(taskId, userIds);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @PutMapping(value="/assign/tasks/{task_id}")
    public PMSTask setUsersToTask(@PathVariable("task_id") Long taskId, 
                @RequestBody @Validated List<Long> userIds) {
        return entityProvider.setUsersToTask(taskId, userIds);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @DeleteMapping(value="/assign/tasks/{task_id}")
    public void removeUsersFromTask(@PathVariable("task_id") Long taskId, 
            @RequestBody List<Long> userIds) {
        entityProvider.removeUsersFromTask(taskId, userIds);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @GetMapping(value="/assign/tasks/{task_id}")
    public List<PMSUser> getUsersByTask(@PathVariable("task_id") Long taskId) {
        return entityProvider.getUsersByTaskId(taskId);
    }
 
    // assign users to project.
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @PostMapping(value="/assign/projects/{project_id}")
    public PMSTask addUsersToProject(@PathVariable("project_id") Long projectId, 
                @RequestBody @Validated List<Long> userIds) {
        return entityProvider.addUsersToProject(projectId, userIds);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @PutMapping(value="/assign/projects/{project_id}")
    public PMSTask setUsersToProject(@PathVariable("project_id") Long projectId, 
                @RequestBody @Validated List<Long> userIds) {
        return entityProvider.setUsersToProject(projectId, userIds);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @DeleteMapping(value="/assign/projects/{project_id}")
    public void removeUsersFromProject(@PathVariable("project_id") Long projectId, 
            @RequestBody List<Long> userIds) {
        entityProvider.removeUsersFromProject(projectId, userIds);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @GetMapping(value="/assign/projects/{project_id}")
    public List<PMSUser> getUsersByProject(@PathVariable("project_id") Long projectId) {
        return entityProvider.getUsersByProject(projectId);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @GetMapping(value="/start/projects/{project_id}")
    public ResponseEntity<PMSProject> startProject(@PathVariable("project_id") Long projectId) {
    	return new ResponseEntity<>(entityProvider.startProject(projectId), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @GetMapping(value="/stop/projects/{project_id}")
    public ResponseEntity<PMSProject> stopProject(@PathVariable("project_id") Long projectId) {
    	return new ResponseEntity<>(entityProvider.stopProject(projectId), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @GetMapping(value="/start/tasks/{task_id}")
    public ResponseEntity<PMSTask> startTask(@PathVariable("task_id") Long taskId) {
    	return new ResponseEntity<>(entityProvider.startTask(taskId), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'technician', 'viewer')")
    @GetMapping(value="/stop/tasks/{task_id}")
    public ResponseEntity<PMSTask> stopTask(@PathVariable("task_id") Long taskId) {
    	return new ResponseEntity<>(entityProvider.stopTask(taskId), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PostMapping("/upload")
	public PMSFile upload(@RequestParam("file") MultipartFile file, 
			@RequestParam(value="display_name", required=false) String displayName, 
			@RequestParam(value="file_type", defaultValue="File") PMSFileType fileType) {
    	return entityProvider.upload(file, displayName, fileType);
	}
}
