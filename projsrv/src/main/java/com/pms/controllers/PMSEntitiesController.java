/**
 * 
 */
package com.pms.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
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

import com.pms.constants.PMSEntityConstants;
import com.pms.constants.PMSRoleName;
import com.pms.entities.PMSComment;
import com.pms.entities.PMSCompany;
import com.pms.entities.PMSProject;
import com.pms.entities.PMSRole;
import com.pms.entities.PMSTag;
import com.pms.entities.PMSTask;
import com.pms.entities.PMSUser;
import com.pms.services.PMSEntityProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jifang
 *
 */
@RestController
@RequestMapping(value="/api/v1/entities", 
            produces="application/json", 
            consumes="application/json")
@Slf4j
public class PMSEntitiesController {
    @Autowired
    private PMSEntityProvider entityProvider;
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/companies")
    public List<PMSCompany> getCompanies() {
        return entityProvider.getCompanies();
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PostMapping(value="/companies")
    public ResponseEntity<PMSCompany> createCompany(@RequestBody @Validated PMSCompany comp) {
        return new ResponseEntity<>(entityProvider.createCompany(comp), HttpStatus.CREATED);
    } 
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/companies/{id}")
    public ResponseEntity<PMSCompany> getCompany(@PathVariable("id") Long id) {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        
        return new ResponseEntity<>(entityProvider.getCompaniesByIds(ids).get(0), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PutMapping(value="/companies/{id}")
    public ResponseEntity<PMSCompany> updateCompany(@PathVariable("id") Long id, 
            @RequestBody @Validated PMSCompany comp) {
        return new ResponseEntity<>(entityProvider.updateCompany(id, comp), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @DeleteMapping(value="/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") Long id) {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        entityProvider.cleanupCompanies(ids);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/companies/{company_id}/projects")
    public ResponseEntity<List<PMSProject>> getProjects(@PathVariable("company_id") Long companyId) {
            return new ResponseEntity<>(entityProvider.getProjectsByCompanyId(companyId), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PostMapping(value="/companies/{company_id}/projects")
    public ResponseEntity<PMSProject> createProject(@PathVariable("company_id") Long companyId, 
            @RequestBody @Validated PMSProject project) {
        return new ResponseEntity<>(entityProvider.createProject(companyId, project), HttpStatus.CREATED);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PutMapping(value="/companies/{company_id}/projects/{project_id}")
    public ResponseEntity<PMSProject> updateProject(@PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId, 
            @RequestBody @Validated PMSProject project) {
        return new ResponseEntity<>(entityProvider.updateProject(companyId, projectId, project), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @DeleteMapping(value="/companies/{company_id}/projects/{project_id}")
    public ResponseEntity<Void> deleteProject(@PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId) {
        List<Long> ids = new ArrayList<>();
        ids.add(projectId);
        entityProvider.cleanupProjects(companyId, ids);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/companies/{company_id}/projects/{project_id}")
    public ResponseEntity<PMSProject> getProject(@PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId) {
        List<Long> ids = new ArrayList<>();
        ids.add(projectId);
        
        return new ResponseEntity<>(entityProvider.getProjectsByIds(ids).get(0), HttpStatus.NO_CONTENT);        
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/companies/{company_id}/projects/{project_id}/tasks")
    public ResponseEntity<List<PMSTask>> getTasks(@PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId) {
    	List<PMSTask> ret = null;
        ret = entityProvider.getTasksByProjectIdAndCompanyId(projectId, companyId);
        
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PostMapping(value="/companies/{company_id}/projects/{project_id}/tasks")
    public ResponseEntity<PMSTask> createTask(@PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId, 
            @RequestBody @Validated PMSTask task) {
        return new ResponseEntity<>(entityProvider.createTask(companyId, projectId, task), HttpStatus.CREATED);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/companies/{company_id}/projects/{project_id}/tasks/{task_id}")
    public ResponseEntity<PMSTask> getTask(@PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId, 
            @PathVariable("task_id") Long taskId) {
        List<Long> ids = new ArrayList<>();
        ids.add(taskId);
        
        return new ResponseEntity<>(entityProvider.getTasksByIds(ids).get(0), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PutMapping(value="/companies/{company_id}/projects/{project_id}/tasks/{task_id}")
    public ResponseEntity<PMSTask> updateTask(@PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId, 
            @PathVariable("task_id") Long taskId,  
            @RequestBody @Validated PMSTask task) {
        return new ResponseEntity<>(entityProvider.updateTask(companyId, projectId, taskId, task), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @DeleteMapping(value="/companies/{company_id}/projects/{project_id}/tasks/{task_id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId, 
            @PathVariable("task_id") Long taskId) {
        List<Long> ids = new ArrayList<>();
        ids.add(taskId);
        entityProvider.cleanupTasks(companyId, projectId, ids);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/companies/{company_id}/projects/{project_id}/comments")
    public ResponseEntity<List<List<PMSComment>>> getCommentsForProject(@PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId, 
            @RequestParam(value="project_only", required=false, defaultValue="false") boolean projectOnly) {
        List<List<PMSComment>> ret = null;
        if (projectOnly == true) {
        	log.debug("only get comments for the project, comments in its tasks are excluded.");
            ret = new ArrayList<>();
            ret.add(entityProvider.getCommentsForProjectOnly(companyId, projectId));
        } else {
        	log.debug("get comments for the project and its tasks.");
            ret = entityProvider.getCommentsByProject(companyId, projectId);
        }
        
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PostMapping(value="/companies/{company_id}/projects/{project_id}/comments")
    public ResponseEntity<PMSComment> createCommentForProject(@PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId, 
            @RequestBody @Validated PMSComment comment) {
        return new ResponseEntity<PMSComment>(entityProvider.createCommentForProject(companyId, projectId, comment), HttpStatus.CREATED);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/companies/{company_id}/projects/{project_id}/comments/{comment_id}")
    public ResponseEntity<PMSComment> getComment(@PathVariable("comment_id") Long commentId, 
            @PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId) {
        List<Long> commentIds = new ArrayList<>();
        commentIds.add(commentId);
      
        return new ResponseEntity<PMSComment>(entityProvider.getComments(companyId, projectId, commentIds).get(0), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PutMapping(value="/companies/{company_id}/projects/{project_id}/comments/{comment_id}")
    public ResponseEntity<PMSComment> updateComment(@PathVariable("comment_id") Long commentId, 
            @PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId,  
            @RequestBody @Validated PMSComment comment) {
        return new ResponseEntity<PMSComment>(entityProvider.updateComment(companyId,projectId, commentId, comment), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @DeleteMapping(value="/companies/{company_id}/projects/{project_id}/comments/{comment_id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("comment_id") Long commentId, 
            @PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId) {
        List<Long> commentIds = new ArrayList<>();
        commentIds.add(commentId);
        entityProvider.cleanupComments(companyId, projectId, commentIds);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/companies/{company_id}/projects/{project_id}/tasks/{task_id}/comments")
    public ResponseEntity<List<PMSComment>> getCommentsByTask(@PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId, 
            @PathVariable("task_id") Long taskId) {
    	
        return new ResponseEntity<>(entityProvider.getCommentsByTask(companyId, projectId, taskId), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PostMapping(value="/companies/{company_id}/projects/{project_id}/tasks/{task_id}/comments")
    public ResponseEntity<PMSComment> createCommentForTask(@PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId, 
            @PathVariable("task_id") Long taskId, 
            @RequestBody @Validated PMSComment comment) {
        return new ResponseEntity<PMSComment>(entityProvider.createCommentForTask(companyId, projectId, taskId, comment), HttpStatus.CREATED);
    }
    /*
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/companies/{company_id}/projects/{project_id}/tasks/{task_id}/comments/{comment_id}")
    public ResponseEntity<PMSComment> getComment(@PathVariable("comment_id") Long commentId, 
            @PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId, 
            @PathVariable("task_id") Long taskId) {
        List<Long> commentIds = new ArrayList<>();
        commentIds.add(commentId);
      
        return new ResponseEntity<PMSComment>(entityProvider.getComments(companyId, projectId, commentIds).get(0), HttpStatus.OK);
    }*/
    /*
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PutMapping(value="/companies/{company_id}/projects/{project_id}/tasks/{task_id}/comments/{comment_id}")
    public ResponseEntity<PMSComment> updateComment(@PathVariable("comment_id") Long commentId, 
            @PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId,
            @PathVariable("task_id") Long taskId, 
            @RequestBody @Valid PMSComment comment, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<PMSComment>(comment, HttpStatus.BAD_REQUEST);
        }
        
        return new ResponseEntity<PMSComment>(entityProvider.updateComment(commentId, comment), HttpStatus.OK);
    }*/
    /*
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @DeleteMapping(value="/companies/{company_id}/projects/{project_id}/tasks/{task_id}/comments/{comment_id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("comment_id") Long commentId, 
            @PathVariable("company_id") Long companyId, 
            @PathVariable("project_id") Long projectId, 
            @PathVariable("task_id") Long taskId) {
        List<Long> commentIds = new ArrayList<>();
        commentIds.add(commentId);
        entityProvider.cleanupComments(companyId, projectId, commentIds);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }*/
    
    // users
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/users")
    public ResponseEntity<List<PMSUser>> getUsers() {
        return new ResponseEntity<>(entityProvider.getUsers(), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PostMapping(value="/users")
    public ResponseEntity<PMSUser> createUser(@RequestBody @Validated PMSUser user, 
    		@RequestParam(name="company_id", required=false) Long companyId) {
        Long compId = companyId;
        if (companyId == null) {
        	compId = Long.valueOf(PMSEntityConstants.kDefaultCompanyId);
        }
        return new ResponseEntity<>(entityProvider.createUser(user, compId), HttpStatus.CREATED);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/users/{user_id}")
    public ResponseEntity<PMSUser> getUser(@PathVariable("user_id") Long userId) {
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        
        return new ResponseEntity<>(entityProvider.getUsersByIds(userIds).get(0), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PutMapping("/users/{user_id}")
    public ResponseEntity<PMSUser> updateUser(@PathVariable("user_id") Long userId, 
    		@RequestParam(name="company_id", required=false) Long companyId,  
            @RequestBody @Validated PMSUser user) {
        return new ResponseEntity<>(entityProvider.updateUser(userId, user, companyId), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @DeleteMapping(value="/users/{user_id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("user_id") Long userId) {
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        entityProvider.deleteUsers(userIds);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/users/{user_id}/projects")
    public ResponseEntity<List<PMSProject>> getProjectsByUserId(@PathVariable("user_id") Long userId) {
        return new ResponseEntity<>(entityProvider.getProjectsByUserId(userId), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/users/{user_id}/tasks")
    public ResponseEntity<List<List<PMSTask>>> getTasksByUserId(@PathVariable("user_id") Long userId) {
        return new ResponseEntity<>(entityProvider.getTasksByUserId(userId), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PostMapping(value="/roles")
    public ResponseEntity<PMSRole> createRole(@RequestBody @Valid PMSRole role, BindingResult result) {
    	if (result.hasErrors()) {
    		return new ResponseEntity<>(role, HttpStatus.BAD_REQUEST);
    	}
    	
    	return new ResponseEntity<>(entityProvider.createRole(role), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/roles")
    public ResponseEntity<List<PMSRole>> getRoles() {
    	return new ResponseEntity<>(entityProvider.getRoles(), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/roles/{role_id}")
    public ResponseEntity<PMSRole> getRole(@PathVariable("role_id") Long roleId) {
    	return new ResponseEntity<>(entityProvider.getRole(roleId), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/roles/{role_name}")
    public ResponseEntity<PMSRole> getRole(@PathVariable("role_name") PMSRoleName roleName) {
    	return new ResponseEntity<>(entityProvider.getRole(roleName), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PutMapping(value="/roles/{role_id}")
    public ResponseEntity<PMSRole> updateRole(@RequestBody @Valid PMSRole role, BindingResult result, 
    		@PathVariable("roleId") Long roleId) {
    	if (result.hasErrors()) {
    		return new ResponseEntity<>(role, HttpStatus.BAD_REQUEST);
    	}
    	
    	return new ResponseEntity<>(entityProvider.updateRole(roleId, role), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PutMapping(value="/roles/{role_name}")
    public ResponseEntity<PMSRole> updateRole(@RequestBody @Valid PMSRole role, BindingResult result, 
    		@PathVariable("role_name") PMSRoleName roleName) {
    	if (result.hasErrors()) {
    		return new ResponseEntity<>(role, HttpStatus.BAD_REQUEST);
    	}
    	
    	return new ResponseEntity<>(entityProvider.updateRole(roleName, role), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @DeleteMapping(value="/roles/{role_id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("role_id") Long roleId) {
    	//entityProvider.deleteRole(roleId);
    	return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/roles/{role_id}/users")
    public ResponseEntity<List<PMSUser>> getRoleUsers(@PathVariable("role_id") Long roleId) {
    	return new ResponseEntity<>(entityProvider.getUsersByRoleId(roleId), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @GetMapping(value="/roles/{role_name}/users")
    public ResponseEntity<List<PMSUser>> getRoleUsers(@PathVariable("role_name") PMSRoleName roleName) {
    	return new ResponseEntity<>(entityProvider.getUsersByRoleName(roleName), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('manager', 'technician', 'admin', 'viewer')")
    @PutMapping(value="/tags")
    public ResponseEntity<List<PMSTag>> updateTags(@RequestBody List<PMSTag> tags) {
    	return new ResponseEntity<>(entityProvider.updateTags(tags), HttpStatus.OK);
    }
    
    
}
