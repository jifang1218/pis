/**
 * 
 */
package com.pms.services;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;

import com.pms.constants.PMSEntityConstants;
import com.pms.constants.PMSFileType;
import com.pms.constants.PMSRoleName;
import com.pms.controllers.exceptions.DeletionFailureException;
import com.pms.controllers.exceptions.DuplicateObjectsException;
import com.pms.controllers.exceptions.RequestValueMismatchException;
import com.pms.controllers.exceptions.ResourceNotFoundException;
import com.pms.entities.PMSComment;
import com.pms.entities.PMSCompany;
import com.pms.entities.PMSFile;
import com.pms.entities.PMSProject;
import com.pms.entities.PMSRole;
import com.pms.entities.PMSTag;
import com.pms.entities.PMSTask;
import com.pms.entities.PMSUser;
import com.pms.repositories.PMSCommentRepo;
import com.pms.repositories.PMSCompanyRepo;
import com.pms.repositories.PMSFileRepo;
import com.pms.repositories.PMSProjectRepo;
import com.pms.repositories.PMSRoleRepo;
import com.pms.repositories.PMSTagRepo;
import com.pms.repositories.PMSTaskRepo;
import com.pms.repositories.PMSUserRepo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jifang
 *
 */

/**
 * Entity Management class. 
 */
@Service
@Transactional
@Slf4j
public class PMSEntityProvider {
    
    @Autowired
    private PMSCompanyRepo compRepo;
    
    @Autowired
    private PMSProjectRepo projRepo;
    
    @Autowired
    private PMSTaskRepo taskRepo;
    
    @Autowired
    private PMSUserRepo userRepo;
    
    @Autowired
    private PMSRoleRepo roleRepo;
    
    @Autowired
    private PMSCommentRepo commentRepo;
    
    @Autowired
    private PMSFileRepo fileRepo;
        
    @Autowired
    private PasswordEncoder passwdEncoder;
    
    @Autowired
    private PMSTagRepo tagRepo;

    /**
     * helper function to find the removed ID set by comparing the old&new sets. 
     * @param oldIds old ID collection. 
     * @param newIds new ID collection. 
     * @return the collection to be removed (from old collection). 
     */
    @SuppressWarnings("unused")
	private List<Long> updateIdSets(List<Long> oldIds, List<Long> newIds) {
        List<Long> beRemovedIdSet = new ArrayList<>();
        
        for (Long oldId : oldIds) {
            if (!newIds.contains(oldId)) {
                beRemovedIdSet.add(oldId);
            }
        }
        
        return beRemovedIdSet;
    }
    
    /**
     * helper function to find the removed string set by comparing the old&new sets.
     * @param oldStrings old string collection. 
     * @param newStrings new string collection. 
     * @return the collection to be removed (from old collection). 
     * @apiNote case-sensitive. 
     */
    @SuppressWarnings("unused")
	private List<String> updateStringSets(List<String> oldStrings, List<String> newStrings) {
        List<String> beRemovedStringSet = new ArrayList<>();
        
        for (String oldString : oldStrings) {
            if (!newStrings.contains(oldString)) {
                beRemovedStringSet.add(oldString);
            }
        }
        
        return beRemovedStringSet;
    }
    
    /**
     * helper function, add a file to db. 
     * will ignore the duplicated file. 
     * @param file file object to be inserted. 
     * @return the saved file object. 
     */
    private PMSFile addFile(PMSFile file) {
    	PMSFile ret = null;
    	
    	if (file == null) {
    		log.debug("file is null, return.");
    		return ret;
    	}
    	
    	if (!fileRepo.existsByRealFilename(file.getRealFilename())) {
    		ret = fileRepo.save(file);
    		log.debug("file saved. displayname:{}\nrealpath:{}\ntype:{}", 
        			file.getDisplayFilename(), file.getRealFilename(), file.getFileType());
    	} else {
    		log.debug("file exists, don't save it.");
    	}
    	
    	return ret;
    }
    
    /**
     * helper function, get the company default avatar.  
     * otherwise create and save the company default avatar. 
     * @return the company default avatar object. 
     */
    private PMSFile getDefaultCompanyAvatar() {
    	PMSFile ret = null;
    	
    	if (fileRepo.existsByRealFilename(PMSEntityConstants.kCompanyDefaultAvatarPath)) {
    		ret = fileRepo.findByRealFilename(PMSEntityConstants.kCompanyDefaultAvatarPath).orElse(ret);
    	} else {
    		ret = new PMSFile(PMSEntityConstants.kCompanyDefaultAvatarPath, PMSFileType.Image);
    		ret.setParentId(PMSEntityConstants.kDefaultFileParentId);
    		ret = fileRepo.save(ret);
    		log.debug("no default company avatar, create it.");
    	}
    	
    	return ret;
    }
    
    /**
     * helper function, get the project default avatar.  
     * otherwise create and save the project default avatar. 
     * @return the project default avatar object. 
     */
    private PMSFile getDefaultProjectAvatar() {
    	PMSFile ret = null;
    	
    	if (fileRepo.existsByRealFilename(PMSEntityConstants.kProjectDefaultAvatarPath)) {
    		ret = fileRepo.findByRealFilename(PMSEntityConstants.kProjectDefaultAvatarPath).orElse(ret);
    	} else {
    		ret = new PMSFile(PMSEntityConstants.kProjectDefaultAvatarPath, PMSFileType.Image);
    		ret.setParentId(PMSEntityConstants.kDefaultFileParentId);
    		ret = fileRepo.save(ret);
    		log.debug("no default project avatar, create it.");
    	}
    	
    	return ret;
    }
    
    /**
     * helper function, get the task default avatar.  
     * otherwise create and save the task default avatar. 
     * @return the task default avatar object. 
     */
    private PMSFile getDefaultTaskAvatar() {
    	PMSFile ret = null;
    	
    	if (fileRepo.existsByRealFilename(PMSEntityConstants.kTaskDefaultAvatarPath)) {
    		ret = fileRepo.findByRealFilename(PMSEntityConstants.kTaskDefaultAvatarPath).orElse(ret);
    	} else {
    		ret = new PMSFile(PMSEntityConstants.kTaskDefaultAvatarPath, PMSFileType.Image);
    		ret.setParentId(PMSEntityConstants.kDefaultFileParentId);
    		ret = fileRepo.save(ret);
    		log.debug("no default task avatar, create it.");
    	}
    	
    	return ret;
    }
    
    /**
     * helper function, get the user's default avatar.  
     * otherwise create and save the user's default avatar. 
     * @return the user's default avatar object. 
     */
    private PMSFile getDefaultUserAvatar() {
    	PMSFile ret = null;
    	
    	if (fileRepo.existsByRealFilename(PMSEntityConstants.kUserDefaultAvatarPath)) {
    		ret = fileRepo.findByRealFilename(PMSEntityConstants.kUserDefaultAvatarPath).orElse(ret);
    	} else {
    		ret = new PMSFile(PMSEntityConstants.kUserDefaultAvatarPath, PMSFileType.Image);
    		ret.setParentId(PMSEntityConstants.kDefaultFileParentId);
    		ret = fileRepo.save(ret);
    		log.debug("no default user avatar, create it.");
    	}
    	
    	return ret;
    }
    
    private PMSRole getRoleByName(PMSRoleName name) {
    	return roleRepo.findByName(name).orElseThrow(()-> {
    		log.debug("cannot find role=" + name.getValue());
    		return new ResourceNotFoundException("cannot find role=" + name.getValue());
    	});
    }
    
    // company
    /**
     * get all companies as a list. 
     * @return the company list. 
     */
    public List<PMSCompany> getCompanies() {
        List<PMSCompany> ret = null;
        
        ret = compRepo.findAll();
        
        return ret; 
    }
    
    // company operations
    /**
     * create a company
     * @param comp company object
     * @return the newly created company object. 
     * @throws DuplicateObjectsException
     * @apiNote this function doesn't process projectIds and userIds fields. 
     */
    public PMSCompany createCompany(PMSCompany comp) throws DuplicateObjectsException {
        PMSCompany ret = null;

        if (compRepo.existsByName(comp.getName())) {
        	log.debug("name " + comp.getName() + " exists, can't create company with this name.");
        	throw new DuplicateObjectsException("company exists with name=" + comp.getName());
        }
        
        if (comp.getAvatar() != null) {
        	PMSFile avatar = comp.getAvatar();
        	avatar.setParentId(comp.getId());
        	addFile(avatar);
        } else {
        	comp.setAvatar(getDefaultCompanyAvatar());
        }
        
        ret = compRepo.save(comp);
        
        return ret;
    }
    
    public List<PMSCompany> getCompaniesByIds(List<Long> companyIds) {
        List<PMSCompany> ret = new ArrayList<PMSCompany>();

        for (Long companyId : companyIds) {
            PMSCompany company = compRepo.findById(companyId).orElseThrow(
                   ()-> {
                	   log.debug("no company with id={}.", companyId);
                	   return new ResourceNotFoundException("No company found with id=" + companyId);
                	   });
            ret.add(company);
        }

        return ret;
    }
 
    public PMSCompany updateCompany(Long id, PMSCompany comp) {
    	PMSCompany ret = compRepo.findById(id).orElseThrow(
                ()->{
                	log.debug("no company with id= {}.", id);
                	return new ResourceNotFoundException("No company found with id=" + id);
                	});
        
    	// change name
    	if (comp.getName() != null) {
    		log.debug("new name != null.");
    		if (!ret.getName().equals(comp.getName())) { // need to change name
    			log.debug("new name {} != old name {}", ret.getName(), comp.getName());
    			if (compRepo.existsByName(comp.getName())) {
    				log.debug("name " + comp.getName() + " exists, can't update company with this name.");
    				throw new DuplicateObjectsException("company name="+ comp.getName() + " exists.");
    			} else {
    				ret.setName(comp.getName());
    			}
    		}
    	}
    	
    	// change desc
    	if (comp.getDesc() != null) {
    		log.debug("new desc != null.");
    		if (!ret.getDesc().equals(comp.getDesc())) { // need to change name
    			log.debug("new desc {} != old desc {}", comp.getDesc(), ret.getDesc());
    			ret.setDesc(comp.getDesc());
    		}
    	}
    	
    	// change address
    	if (comp.getAddress() != null) {
    		log.debug("new address != null.");
    		if (!ret.getAddress().equals(comp.getAddress())) { // need to update address
    			log.debug("new address {} != old address {}", comp.getAddress(), ret.getAddress());
    			ret.setAddress(comp.getAddress());
    		}
    	}
    	
    	// change phone
    	if (comp.getPhone() != null) {
    		log.debug("new phone != null.");
    		if (!ret.getPhone().equals(comp.getPhone())) { // need to update phone number
    			log.debug("new phone {} != old phone {}", comp.getPhone(), ret.getPhone());
    			ret.setPhone(comp.getPhone());
    		}
    	}
    	
    	// change website
    	if (comp.getWebsite() != null) {
    		log.debug("new website != null.");
    		if (!ret.getWebsite().equals(comp.getWebsite())) { // need to update website
    			log.debug("new website {} != old website {}", comp.getWebsite(), ret.getWebsite());
    			ret.setWebsite(comp.getWebsite());
    		}
    	}
    	
    	// change avatar
        if (comp.getAvatar() != null) {
        	// remove old one if exists.
        	PMSFile oldAvatar = ret.getAvatar();
        	if (oldAvatar != null &&
        		oldAvatar.getParentId().longValue() != PMSEntityConstants.kDefaultFileParentId) {
        		deleteFile(oldAvatar.getId());
        	}
        	PMSFile newAvatar = comp.getAvatar();
        	newAvatar.setParentId(comp.getId());
            ret.setAvatar(newAvatar);
        }
        if (comp.getDesc() != null) {
            ret.setDesc(comp.getDesc());
        }
        compRepo.save(ret);
        
        return ret;
    }
    
    public void cleanupCompanies(List<Long> companyIds) {
        for (Long companyId : companyIds) {
            PMSCompany comp = compRepo.findById(companyId).orElse(null);
            if (comp == null) {
            	continue;
            }
            List<Long> projectIds = comp.getProjectIds();
            cleanupProjects(companyId, projectIds);
            compRepo.deleteById(companyId);
        }
    }
    
    // project operations
    public List<PMSProject> getProjectsByCompanyId(Long companyId) {
        if (!compRepo.existsById(companyId)) {
        	log.error("No company found with id={}", companyId);
            throw new ResourceNotFoundException("No company found with id=" + companyId);
        }
        
        List<PMSProject> projects = projRepo.findAllByCompanyId(companyId);
        
        return projects;
    }
    
    public List<PMSProject> getProjectsByIds(List<Long> projectIds) {
        List<PMSProject> ret = new ArrayList<>();
        
        for (Long projectId : projectIds) {
        	log.error("No project with id=" + projectId);
            PMSProject project = projRepo.findById(projectId).orElseThrow(()-> {
            	log.debug("no project with id={}.", projectId);
                return new ResourceNotFoundException("No project found with id=" + projectId);
                });
            ret.add(project);
        }
            
        return ret;
    }
    
    /**
     * create a project within the company. 
     * @param companyId the project will be created in. 
     * @param project the project object
     * @return the newly create project. 
     */
    public PMSProject createProject(Long companyId, PMSProject project) {
    	// check if the company exists
    	if (!compRepo.existsById(companyId)) {
    		log.debug("No company found with id=" + companyId);
    		throw new ResourceNotFoundException("No company found with id=" + companyId);
    	}
    	
    	project.setCompanyId(companyId);
    	
    	// check if the project exists. 
    	if (projRepo.existsByNameAndCompanyId(project.getName(), companyId)) {
    		log.debug("project with name=" + project.getName() + " exists, can't create project with this name.");
           	throw new DuplicateObjectsException("project exists with name=" 
        				+ project.getName() + " and company_id=" + companyId);
        }
        
        if (project.getAvatar() != null) {
        	log.debug("project's avatar != null, will use the customized avatar.");
        	PMSFile avatar = project.getAvatar();
        	avatar.setParentId(project.getId());
        	addFile(avatar);
        } else {
        	log.debug("project's avatar == null, use project default avatar.");
        	project.setAvatar(getDefaultProjectAvatar());
        }
        project = projRepo.save(project);
        
        // update company.
        PMSCompany company = compRepo.findById(companyId)
    			.orElseThrow(()->{
    			log.debug("no company with id={}.", companyId);	
    			return new ResourceNotFoundException("No company found with id=" + companyId);
    			});
        company.addProjectId(project.getId());
        compRepo.save(company);
        
        return project;
    }

    public PMSProject updateProject(Long companyId, Long projectId, PMSProject project) {
    	if (!compRepo.existsById(companyId)) {
    		log.debug("No company with id=" + companyId);
    		throw new ResourceNotFoundException("No company found with id=" + companyId);
    	}
    	
    	PMSProject ret = projRepo.findById(projectId).orElseThrow(
                ()->{
                log.debug("no project with id={}.", projectId);	
                return new ResourceNotFoundException("No project found with id=" + projectId);
                });
    	// change project name. 
        if ((project.getName() != null) && (ret.getName().compareTo(project.getName()) != 0)) {
        	log.debug("new name != null, new name != old name, need to change project name.");
        	// need to change project name. 
        	if (projRepo.existsByNameAndCompanyId(project.getName(), companyId)) {
        		log.debug("new name:{}, duplicate to the existing name.", project.getName());
        		throw new DuplicateObjectsException("project " + ret.getName() + " exists in the company with id=" + companyId);
        	} else {
        		ret.setName(project.getName());
        	}   	
        }
        if (project.getPriority() != null) {
        	ret.setPriority(project.getPriority());
        }
        if (project.getAvatar() != null) {
        	PMSFile oldAvatar = ret.getAvatar();
        	if (oldAvatar != null) {
	        	if (oldAvatar.getParentId().longValue() != PMSEntityConstants.kDefaultFileParentId) {
	        		deleteFile(oldAvatar.getId());
	        	}
        	}
        	PMSFile newAvatar = project.getAvatar();
        	newAvatar.setParentId(projectId);
            ret.setAvatar(newAvatar);
        }
        if (project.getDesc() != null) {
            ret.setDesc(project.getDesc());
        }
        return projRepo.save(ret);
    }

    public void cleanupProjects(Long companyId, List<Long> projectIds) {
    	if (!compRepo.existsById(companyId)) {
    		log.debug("No company with id=" + companyId);
    		throw new ResourceNotFoundException("No company found with id=" + companyId);
    	}
    	
    	if (projectIds.size() == 0) {
    		return;
    	}

    	PMSCompany company = compRepo.findById(companyId).orElseGet(null);
    	List<PMSProject> projects = projRepo.findAllById(projectIds);
    	for (PMSProject project : projects) {
    		// check if other projects depend on it.
    		List<PMSProject> allCompanyProjects = this.getProjectsByCompanyId(companyId);
    		for (PMSProject allCompanyProject : allCompanyProjects) {
        		if (allCompanyProject.getDependentProjectIds().contains(project.getId())) {
        			log.debug("project {} depends on {}, cannot delete it.", allCompanyProject.getId(), project.getId());
        			throw new DeletionFailureException("Cannot delete the project (" + project.getId() + ") as project " + allCompanyProject.getId() + " depends on it.");
        		}
        	}

    		// delete its tasks
        	List<Long> taskIds = project.getTaskIds();
        	Long projectId = project.getId();
            cleanupTasks(companyId, projectId, taskIds);
            cleanupDefaultTask(project);
            
            // update company, remove project from the company
            company.removeProjectId(projectId);
            compRepo.save(company);
            
            // delete project
            projRepo.deleteById(projectId);
        }
    }
    
    private void cleanupDefaultTask(PMSProject project) {
        if (project == null) {
            return;
        }
        PMSTask defaultTask = project.getDefaultTask();
        if (defaultTask != null) {
        	// delete avatar. 
        	PMSFile avatar = defaultTask.getAvatar();
        	if (avatar != null &&
        		avatar.getParentId().longValue() != PMSEntityConstants.kDefaultFileParentId) {
        		deleteFile(avatar.getId());
        	}
        	List<PMSFile> attachments = defaultTask.getAttachments();
        	for (PMSFile attachment : attachments) {
        		deleteFile(attachment.getId());
        	}
            List<Long> commentIds = defaultTask.getCommentIds();
            cleanupComments(project.getCompanyId(), project.getId(), commentIds);
        }
        
        // default task will be deleted when the project is deleted.
        // no need to delete the task manually.
        // taskRepo.deleteById(defaultTask.getId());
    }

    public PMSProject addDependentProjectIds(Long projectId, List<Long> dependentProjectIds) {
        PMSProject ret = projRepo.findById(projectId).orElseThrow(
                ()->{
                log.debug("no project with id={}.", projectId);	
                return new ResourceNotFoundException("No project found with id=" + projectId);
                });

        for (Long dependentProjectId : dependentProjectIds) {
            // a project cannot dependent on itself.
            if (projRepo.existsById(dependentProjectId) &&
            		!ret.getId().equals(dependentProjectId)) {
                ret.addDependentProjectId(dependentProjectId);
            }
        }
        projRepo.save(ret);
        
        return ret;
    }

    public PMSProject setDependentProjectIds(Long projectId, List<Long> dependentProjectIds) {
        PMSProject ret = projRepo.findById(projectId).orElseThrow(
                ()->{
                log.debug("no project with id={}.", projectId);	
                return new ResourceNotFoundException("No project found with id=" + projectId);
                });
        
        ret.getDependentProjectIds().clear();
        ret = addDependentProjectIds(projectId, dependentProjectIds);
        
        return ret;
    }

    public PMSProject removeDependentProjectIds(Long projectId, List<Long> dependentProjectIds) {
        PMSProject ret = projRepo.findById(projectId).orElseThrow(
                ()->{
                log.debug("no project with id={}.", projectId);
                return new ResourceNotFoundException("No project found with id=" + projectId);
                });
        
        List<Long> currentDependentIds = ret.getDependentProjectIds();
        for (Long dependentProjectId : dependentProjectIds) {
            if (currentDependentIds.contains(dependentProjectId)) {
                ret.removeDependentProjectId(dependentProjectId);
            }
        }
        projRepo.save(ret);
        
        return ret;
    }
    
    public List<PMSProject> getDependentProjectsById(Long projectId) {
        List<PMSProject> projects = new ArrayList<PMSProject>();
        PMSProject proj = projRepo.findById(projectId).orElseThrow(
                ()->{
                	log.debug("no project with id={}.", projectId);
                	return new ResourceNotFoundException("No project found with id=" + projectId);
                	});
        
        List<Long> projIds = proj.getDependentProjectIds();
        for (Long projId : projIds) {
            PMSProject dep = projRepo.findById(projId).orElseGet(null);
            if (dep != null) {
                projects.add(dep);
            }
        }
        
        return projects;
    }
    
    public boolean isUserExistsInProject(Long userId, Long projectId) {
        boolean ret = false;
        
        // check if the user exists. 
        if (!userRepo.existsById(userId)) {
        	log.debug("No user found with id=" + userId);
        	throw new ResourceNotFoundException("No user found with id=" + userId);
        }
        
        PMSProject project = projRepo.findById(projectId).orElseThrow(
                ()->{
                log.debug("no project with id={}.", projectId);
                return new ResourceNotFoundException("No project found with id=" + projectId);
                });
        
        do {
        	// check if the user is assigned to project
            if (isUserExistsInTask(userId, project.getDefaultTask().getId())) {
                ret = true;
                break;
            }
            // check if the user is assigned to the project's tasks. 
            List<Long> taskIds = project.getTaskIds();
            for (Long taskId : taskIds) {
                if (isUserExistsInTask(userId, taskId)) {
                    ret = true;
                    break;
                }
            }
        } while (false);

        return ret;
    }
    
    public boolean isUserExistsInTask(Long userId, Long taskId) {
    	if (!userRepo.existsById(userId)) {
    		log.debug("no user with id={}", userId);
    		throw new ResourceNotFoundException("No user found with id=" + userId);
    	}
    	
    	PMSTask task = taskRepo.findById(taskId).orElseThrow(
    			()->{
    				log.debug("no task with id={}.", taskId);
    				return new ResourceNotFoundException("No task found with id=" + taskId);
    			});
    	
    	boolean ret = false;
    	List<Long> userIds = task.getUserIds();
    	if (userIds.contains(userId)) {
    		ret = true;
    	} 
    	
    	return ret;
    }
    
    // task operations
    // default task isn't included in the return list. 
    public List<PMSTask> getTasksByProjectIdAndCompanyId(Long projId, Long companyId) {
        if (!projRepo.existsByIdAndCompanyId(projId, companyId)) {
        	log.debug("no project found with id={}, compan_id={}", projId, companyId);
            throw new ResourceNotFoundException("No project found with id=" + projId + 
            		", and company_id=" + companyId);
        }
        
        return taskRepo.findAllByProjectId(projId);
    }
    
    public List<PMSTask> getTasksByIds(List<Long> taskIds) {
        List<PMSTask> ret = new ArrayList<>();
        
        for (Long taskId : taskIds) {
            PMSTask task = taskRepo.findById(taskId).orElse(null);
            if (task != null) {
                ret.add(task);
            } else {
            	log.debug("task id={} doesn't exist.", taskId);
            }
        }
        
        return ret;
    }

    public PMSTask createTask(Long companyId, Long projectId, PMSTask task) {
    	// check if the company exists. 
    	if (!compRepo.existsById(companyId)) {
    		log.debug("no company with id=" + companyId);
    		throw new ResourceNotFoundException("No company found with id=" + companyId);
    	}
    	
    	// check if the project exists. 
    	if (!projRepo.existsByIdAndCompanyId(projectId, companyId)) {
    		log.debug("no project with id=" + projectId + " in company with id=" + companyId);
    		throw new ResourceNotFoundException("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    	}
    	
    	// verify if the task doesn't exist. 
    	if (taskRepo.existsByNameAndProjectId(task.getName(), projectId)) {
    		log.debug("task name={} already exists, cannot create task with this name in project={}", task.getName(), projectId);
    		throw new DuplicateObjectsException("task exists with name=" 
    					+ task.getName() + " project_id=" + projectId + " , and company_id=" + companyId);
    	}
        
        // save avatar
        if (task.getAvatar() != null) {
        	PMSFile avatar = task.getAvatar();
        	avatar.setParentId(task.getId());
        	addFile(avatar);
        } else {
        	task.setAvatar(getDefaultTaskAvatar());
        }
        
        // save task
        task.setProjectId(projectId);
        task = taskRepo.save(task);
        
        // update project
        PMSProject project = projRepo.findById(projectId).orElseThrow(
        		()-> {
        			log.debug("no project with id={}.", projectId);
        			return new ResourceNotFoundException("No project found with id=" + projectId);
        		});
        project.addTaskId(task.getId());
        projRepo.save(project);
        
        return task;
    }
    
    public PMSTask updateTask(Long companyId, Long projectId, Long taskId, PMSTask task) {
    	// check if the company exists. 
    	if (!compRepo.existsById(companyId)) {
    		log.debug("no company found with id=" + companyId);
    		throw new ResourceNotFoundException("No company found with id=" + companyId);
    	}
    	
    	// check if the project exists. 
    	if (!projRepo.existsByIdAndCompanyId(projectId, companyId)) {
    		log.debug("no project with id={} found in company={}", projectId, companyId);
    		throw new ResourceNotFoundException("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    	}
    	
    	// verify if the task exists. 
    	if (!taskRepo.existsByIdAndProjectId(taskId, projectId)) {
    		log.debug("no task with id={} exists in project={}", taskId, projectId);
    		throw new ResourceNotFoundException("No task found with id=" + taskId 
    					+ " in project_id=" + projectId + " , and company_id=" + companyId);
    	}
    	
    	PMSTask oldTask = taskRepo.findById(taskId).orElseThrow(
                ()->{
                	log.debug("no task with id={}.", taskId);
                	return new ResourceNotFoundException("No task found with id=" + taskId);
                });
    	// update name
    	if (task.getName() != null 
    			&& !task.getName().equals(oldTask.getName())) { // need to update name. 
    		if (taskRepo.existsByNameAndProjectId(task.getName(), projectId)) { // new name exists. won't update. 
    			log.debug("task with new name={} exists.", task.getName());
    			throw new DuplicateObjectsException("new name " + task.getName() + " exists.");
    		}
    		oldTask.setName(task.getName());
    	}
    	
    	// desc
    	if (task.getDesc() != null) {
    		oldTask.setDesc(task.getDesc());
    	}
    	
    	if (task.getAvatar() != null) {
    		PMSFile oldAvatar = oldTask.getAvatar();
    		if (oldAvatar.getParentId().longValue() != PMSEntityConstants.kDefaultFileParentId) {
    			fileRepo.deleteById(oldAvatar.getId());
    		}
    		PMSFile newAvatar = task.getAvatar();
    		newAvatar.setParentId(oldTask.getId());
    		oldTask.setAvatar(newAvatar);
        }
        
    	// start date
    	if (task.getStartDate() != null
    			&& task.getStartDate().longValue() != oldTask.getStartDate().longValue()) {
    		oldTask.setStartDate(task.getStartDate());
    	}
        
        // end date
    	if (task.getEndDate() != null
    			&& task.getEndDate().longValue() != oldTask.getEndDate().longValue()) {
    		oldTask.setEndDate(task.getEndDate());
    	}
    	
    	// priority
    	if (task.getPriority() != null 
    			&& !task.getPriority().equals(oldTask.getPriority())) {
    		oldTask.setPriority(task.getPriority());
    	}
    	
    	// status
    	if (task.getStatus() != null
    			&& !task.getStatus().equals(oldTask.getStatus())) {
    		oldTask.setStatus(task.getStatus());
    	}
        
        return taskRepo.save(oldTask);
    }
    
    public void cleanupTasks(Long companyId, Long projectId, List<Long> taskIds) {
    	// check if the company exists. 
    	if (!compRepo.existsById(companyId)) {
    		log.debug("no company found with id=" + companyId);
    		throw new ResourceNotFoundException("No company found with id=" + companyId);
    	}
    	
    	// check if the project exists. 
    	PMSProject project = projRepo.findByIdAndCompanyId(projectId, companyId)
    			.orElseThrow(()->{
    				log.debug("no project with id={} in company={}.", projectId, companyId);
    				return new ResourceNotFoundException("no project with id=" + projectId 
    						+ " ,in company=" + companyId);
    			});
    	
        if (taskIds.size() == 0) {
            return;
        }
        
        List<PMSTask> tasks = this.getTasksByIds(taskIds);
        for (PMSTask task : tasks) {
        	// verify project_id
        	if (task.getProjectId().longValue() != projectId.longValue()) {
        		log.debug("task with id={}, its project_id={}, mismatch with the projectId={}.", 
        				task.getId(), task.getProjectId(), projectId);
        		throw new RequestValueMismatchException("task with id=" + task.getId() 
        				+ " ,its project_id=" + task.getProjectId() 
        				+ " ,mismatch with the projectId=" + projectId);
        	}
        		
        	// check if there is dependency issue.
        	List<Long> allTaskIds = project.getTaskIds();
        	List<PMSTask> allTasks = this.getTasksByIds(allTaskIds);
        	for (PMSTask allTask : allTasks) {
        	    // if one of the dependent sets of other tasks contain the task, it can't be removed.
        		if (!allTask.getId().equals(task.getId()) 
        		        && allTask.getDependentTaskIds().contains(task.getId())) {
        			log.debug("Cannot delete the task={} as other tasks(id={}} depend on it.", task.getId(), allTask.getId());
        			throw new DeletionFailureException("Cannot delete the task (" + task.getId() + ") as other tasks depend on it.");
        		}
        	}
        	
        	// delete avatar
        	PMSFile avatar = task.getAvatar();
        	if (avatar != null &&
        		avatar.getParentId().longValue() != PMSEntityConstants.kDefaultFileParentId) {
        		deleteFile(avatar.getId());
        	}
        	
        	// delete attachments
        	List<PMSFile> attachments = task.getAttachments();
        	for (PMSFile attachment : attachments) {
        		deleteFile(attachment.getId());
        	}
    		
    		// delete its comments
        	cleanupComments(companyId, projectId, task.getCommentIds());
        	
        	// we won't delete the project default task as it is managed by the project.
        	if (task.getProjectId() != PMSEntityConstants.kDefaultTaskProjectId) {
        	    taskRepo.deleteById(task.getId());
        	}
            
            // update project, remove task from the project
            project.removeTaskId(task.getId());
            projRepo.save(project);
        }
    }
    
    public PMSTask addDependentTasks(Long taskId, List<Long> dependentTaskIds) {
        PMSTask ret = taskRepo.findById(taskId).orElseThrow(
                ()->{
                	log.debug("no task found with id={}.", taskId);
                	return new ResourceNotFoundException("No task found with id=" + taskId);
                });

        for (Long dependentTaskId : dependentTaskIds) {
            // a task cannot dependent on itself.
            if (taskRepo.existsById(dependentTaskId) 
                    && !ret.getId().equals(dependentTaskId)) {
                ret.addDependentTaskId(dependentTaskId);
            }
        }
        
        return taskRepo.save(ret);
    }
    
    public PMSTask setDependentTasks(Long taskId, List<Long> dependentTaskIds) {
        PMSTask ret = taskRepo.findById(taskId).orElseThrow(
                ()->{
                	log.debug("no task found with id={}.", taskId);
                	return new ResourceNotFoundException("No task found with id=" + taskId);
                });
        
        ret.getDependentTaskIds().clear();
        for (Long dependentTaskId : dependentTaskIds) {
            // a task cannot dependent on itself.
            if (taskRepo.existsById(dependentTaskId) 
                    && !ret.getId().equals(dependentTaskId)) {
                ret.addDependentTaskId(dependentTaskId);
            }
        }
        taskRepo.save(ret);
        
        return ret;
    }

    public PMSTask removeDependentTasks(Long taskId, List<Long> dependentTaskIds) {
        PMSTask ret = taskRepo.findById(taskId).orElseThrow(
                ()->new ResourceNotFoundException("No task found with id=" + taskId));
        
        List<Long> currentDependentIds = ret.getDependentTaskIds();
        for (Long dependentTaskId : dependentTaskIds) {
            if (currentDependentIds.contains(dependentTaskId)) {
                ret.removeDependentTaskId(dependentTaskId);
            }
        }
        return taskRepo.save(ret);
    }
    
    public List<PMSTask> getDependentTasks(Long taskId) {
        List<PMSTask> tasks = new ArrayList<>();
        
        PMSTask task = taskRepo.findById(taskId).orElseThrow(
                ()->{
                	log.debug("no task found with id={}.", taskId);
                	return new ResourceNotFoundException("No task found with id=" + taskId);
                });
        
        List<Long> dependentTaskIds = task.getDependentTaskIds();
        for (Long dependentTaskId : dependentTaskIds) {
            PMSTask dependentTask = taskRepo.findById(dependentTaskId).orElseGet(null);
            if (dependentTask != null) {
                tasks.add(dependentTask);
            }
        }
        
        return tasks;
    }

    // comments operation
    public PMSComment createCommentForProject(Long companyId, Long projectId, PMSComment comment) {
    	// check if the company exists. 
    	if (!compRepo.existsById(companyId)) {
    		log.debug("No company found with id=" + companyId);
    		throw new ResourceNotFoundException("No company found with id=" + companyId);
    	}
    	
    	// check if the project exists. 
    	if (!projRepo.existsByIdAndCompanyId(projectId, companyId)) {
    		log.debug("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    		throw new ResourceNotFoundException("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    	}
    	
    	PMSProject project = projRepo.findById(projectId)
    			.orElseThrow(()->{
    				log.debug("No project found with id=" + projectId);
    				return new ResourceNotFoundException("No project found with id=" + projectId);
    			});
    	
    	// get default task, will create comment to default task. 
    	PMSTask task = project.getDefaultTask();
    	comment.setTaskId(task.getId());
    	commentRepo.save(comment);
    	
    	// update task.
    	task.addCommentId(comment.getId());
    	taskRepo.save(task);
    	
    	return comment;
    }
    
    public PMSComment createCommentForTask(Long companyId, Long projectId, Long taskId, PMSComment comment) {
    	// check if the company exists. 
    	if (!compRepo.existsById(companyId)) {
    		log.debug("No company found with id=" + companyId);
    		throw new ResourceNotFoundException("No company found with id=" + companyId);
    	}
    	
    	// check if the project exists. 
    	if (!projRepo.existsByIdAndCompanyId(projectId, companyId)) {
    		log.debug("No project found with id=" + projectId 
    				+ " ,in company_id=" + companyId);
    		throw new ResourceNotFoundException("No project found with id=" + projectId 
					+ " ,in company_id=" + companyId);
    	}
    	
    	// check if the task exists. 
    	if (!taskRepo.existsByIdAndProjectId(taskId, projectId)) {
    		log.debug("No task found with id=" + taskId 
    				+ " with project_id=" + projectId 
    				+ " with company_id=" + companyId);
    		throw new ResourceNotFoundException("No task found with id=" + taskId 
    				+ " with project_id=" + projectId 
    				+ " with company_id=" + companyId);
    	}
    	
    	// create comment to taskId. 
    	PMSTask task = taskRepo.findById(taskId)
    			.orElseThrow(()->{
    				log.debug("No task found with id=" + taskId 
        					+ " with project_id=" + projectId 
        					+ " with company_id=" + companyId);
    				return new ResourceNotFoundException("No task found with id=" + taskId 
    					+ " with project_id=" + projectId 
    					+ " with company_id=" + companyId);
    			});
    	comment.setTaskId(taskId);
    	commentRepo.save(comment);
    	
    	// update task.
    	task.addCommentId(comment.getId());
    	taskRepo.save(task);
    	
    	return comment;
    }

    public void cleanupComments(Long companyId, Long projectId, List<Long> commentIds) {
    	// check if the company exists. 
    	if (!compRepo.existsById(companyId)) {
    		log.debug("No company found with id=" + companyId);
    		throw new ResourceNotFoundException("No company found with id=" + companyId);
    	}
    	
    	// check if the project exists. 
    	if (!projRepo.existsByIdAndCompanyId(projectId, companyId)) {
    		log.debug("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    		throw new ResourceNotFoundException("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    	}
    	
        List<PMSComment> comments = commentRepo.findAllById(commentIds);
        for (PMSComment comment : comments) {
        	// delete files
        	List<PMSFile> attachments = comment.getAttachments();
        	for (PMSFile attachment : attachments) {
        		deleteFile(attachment.getId());
        	}
        	
        	// update task
        	PMSTask task = taskRepo.findById(comment.getTaskId()).orElse(null);
        	if (task == null) {
        		log.debug("task == null, skip it.");
        		continue;
        	}
        	task.removeCommentId(comment.getId());
        	taskRepo.save(task);
        	
        	commentRepo.delete(comment);
        }        
    }
    
    // return: [task0, task1, ...]
    public List<PMSComment> getCommentsByTask(Long companyId, Long projectId, Long taskId) {
    	// check if the company exists. 
    	if (!compRepo.existsById(companyId)) {
    		log.debug("No company found with id=" + companyId);
    		throw new ResourceNotFoundException("No company found with id=" + companyId);
    	}
    	
    	// check if the project exists. 
    	if (!projRepo.existsByIdAndCompanyId(projectId, companyId)) {
    		log.debug("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    		throw new ResourceNotFoundException("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    	}
    	
    	// check if the task exists. 
    	PMSTask task = taskRepo.findById(taskId).orElseThrow(
                ()->{
                	log.debug("No task found with id=" + taskId);
                	return new ResourceNotFoundException("No task found with id=" + taskId);
                });
    	
        List<PMSComment> ret = new ArrayList<>();
        List<Long> commentIds = task.getCommentIds();
        for (Long commentId : commentIds) {
            PMSComment comment = commentRepo.findById(commentId).orElseGet(null);
            if (comment != null) {
                ret.add(comment);
            } else {
            	log.debug("comment with id={} is null.", commentId);
            }
        }
        
        return ret;
    }
    
    public List<PMSComment> getComments(Long companyId, Long projectId, List<Long> commentIds) {
    	// check if the company exists. 
    	if (!compRepo.existsById(companyId)) {
    		log.debug("No company found with id=" + companyId);
    		throw new ResourceNotFoundException("No company found with id=" + companyId);
    	}
    	
    	// check if the project exists. 
    	if (!projRepo.existsByIdAndCompanyId(projectId, companyId)) {
    		log.debug("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    		throw new ResourceNotFoundException("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    	}
    	
        List<PMSComment> ret = null;
        
        if (commentIds == null) {
            ret = commentRepo.findAll();
        } else {
            ret = new ArrayList<>();
            for (Long commentId : commentIds) {
                PMSComment comment = commentRepo.findById(commentId).orElseThrow(
                        ()-> {
                        	log.debug("No comment found with id=" + commentId);
                        	return new ResourceNotFoundException("No comment found with id=" + commentId);
                        });
                ret.add(comment);
            }
        }
        
        return ret;
    } 
    
    // return: [comment0, comment1, ...]
    public List<PMSComment> getCommentsForProjectOnly(Long companyId, Long projectId) {
    	// check if the company exists. 
    	if (!compRepo.existsById(companyId)) {
    		log.debug("No company found with id=" + companyId);
    		throw new ResourceNotFoundException("No company found with id=" + companyId);
    	}
    	
    	// check if the project exists. 
    	if (!projRepo.existsByIdAndCompanyId(projectId, companyId)) {
    		log.debug("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    		throw new ResourceNotFoundException("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    	}
    	
        PMSProject project = projRepo.findById(projectId).orElseThrow(()-> {
                	log.debug("No project found with id=" + projectId);
                	return new ResourceNotFoundException("No project found with id=" + projectId);
                });
        
        PMSTask task = project.getDefaultTask();
        
        return getCommentsByTask(companyId, projectId, task.getId());
    }

    // return: [project, task0, task1, task2...]
    public List<List<PMSComment>> getCommentsByProject(Long companyId, Long projectId) {
    	// check if the company exists. 
    	if (!compRepo.existsById(companyId)) {
    		log.debug("No company found with id=" + companyId);
    		throw new ResourceNotFoundException("No company found with id=" + companyId);
    	}
    	
    	// check if the project exists. 
    	if (!projRepo.existsByIdAndCompanyId(projectId, companyId)) {
    		log.debug("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    		throw new ResourceNotFoundException("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    	}
    	
    	PMSProject project = projRepo.findById(projectId)
    			.orElseThrow(()-> {
    				log.debug("No project found with id=" + projectId);
    				return new ResourceNotFoundException("No project found with id=" + projectId);
    			});
    	
    	List<List<PMSComment>> ret = new ArrayList<>();

    	// add comments of tasks.
    	// 1. add project comments
    	List<PMSComment> projectComments = getCommentsForProjectOnly(companyId, projectId);
    	ret.add(projectComments);
    	
    	// 2. add tasks comments
    	List<Long> taskIds = project.getTaskIds();
    	for (Long taskId : taskIds) {
    		List<PMSComment> comments = getCommentsByTask(companyId, projectId, taskId);
    		ret.add(comments);
    	}

    	return ret;
    }

    public PMSComment updateComment(Long companyId, Long projectId, Long commentId, PMSComment comment) {
    	// check if the company exists. 
    	if (!compRepo.existsById(companyId)) {
    		log.debug("No company found with id=" + companyId);
    		throw new ResourceNotFoundException("No company found with id=" + companyId);
    	}
    	
    	// check if the project exists. 
    	if (!projRepo.existsByIdAndCompanyId(projectId, companyId)) {
    		log.debug("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    		throw new ResourceNotFoundException("No project found with id=" + projectId 
					+ " in company_id=" + companyId);
    	}
    	
    	PMSComment ret = commentRepo.findById(comment.getId())
    			.orElseThrow(()-> {
    				log.debug("No comment found with id=" + comment.getId());
    				return new ResourceNotFoundException("No comment found with id=" + comment.getId());
    			});
    	
    	if (comment.getTitle() != null) {
    		ret.setTitle(comment.getTitle());
    	}
    	if (comment.getDesc() != null) {
    		ret.setDesc(comment.getDesc());
    	}
    	
    	if (comment.getTaskId() != null) {
    		ret.setTaskId(comment.getTaskId());
    	}
    	commentRepo.save(ret);
    	
    	return ret;
    }
    
    public PMSUser createUser(PMSUser user, Long companyId) {
    	PMSCompany comp = compRepo.findById(companyId).orElseThrow(
        		()-> {
        			log.debug("No company found with id=" + companyId);
        			return new ResourceNotFoundException("No company found with id=" + companyId);
        		});
    	
    	// check if the user exists. 
    	if (userRepo.existsByEmail(user.getEmail())) {
    		log.debug("user email=" + user.getEmail() + " already exists.");
    		throw new DuplicateObjectsException("user email=" + user.getEmail() + " already exists.");
    	}
    	
    	if (user.getPassword() != null) {
    		user.setPassword(passwdEncoder.encode(user.getPassword()));
    	}
    	
    	if (user.getAvatar() != null) {
    		user.getAvatar().setParentId(user.getId());
    		fileRepo.save(user.getAvatar());
    	} else {
    		log.debug("avatar doesn't specify so use the default avatar.");
    		user.setAvatar(getDefaultUserAvatar());
    	}
    	
    	// process roles. we won't create new instance, instead we use existing roles. 
    	List<PMSRole> existingRoles = new ArrayList<>();
    	List<PMSRole> newRoles = user.getRoles();
    	for (PMSRole newRole : newRoles) {
    		PMSRole existingRole = this.getRoleByName(newRole.getName());
    		existingRoles.add(existingRole);
    		log.debug("role name: {}", newRole.getName());
    	}
    	user.setRoles(existingRoles);
    	
    	PMSUser ret = userRepo.save(user);
    	
    	comp.getUserIds().add(ret.getId());
        compRepo.save(comp);
    	
        return ret;
    }
    
    public List<PMSUser> getUsersByProject(Long projectId) {
        PMSProject project = projRepo.findById(projectId).orElseThrow(
                ()->new ResourceNotFoundException("No project found with id=" + projectId));
        
        List<PMSUser> ret = null;
        
        List<Long> taskIds = project.getTaskIds();
        taskIds.add(project.getDefaultTask().getId());
        Set<PMSUser> usersSet = new HashSet<>();
        for (Long taskId : taskIds) {
            List<PMSUser> users = getUsersByTaskId(taskId);
            usersSet.addAll(users);
        }
        
        ret = new ArrayList<>(usersSet); 
        
        return ret;
    }
    
    public List<PMSUser> getUsersByTaskId(Long taskId) {
        PMSTask task = taskRepo.findById(taskId).orElseThrow(
                ()->new ResourceNotFoundException("No task found with id=" + taskId));
        
        List<PMSUser> ret = getUsersByIds(task.getUserIds());
        
        return ret;
    }
    
    public List<PMSUser> getUsers() {
        return userRepo.findAll();
    }
    
    public List<PMSUser> getCompanyUsers() {
    	return userRepo.findCompanyUsers();
    }
    
    public List<PMSUser> getUsersByIds(List<Long> ids) {
        List<PMSUser> ret = new ArrayList<>();
        
        for (Long id : ids) {
            PMSUser user = userRepo.findById(id).orElseThrow(
                        () -> {
                        	log.debug("No user found with id="+id);
                        	return new ResourceNotFoundException("No user found with id="+id);
                        });
            if (!ret.contains(user)) {
                ret.add(user);
            }
        }
        
        return ret;
    }
    
    public PMSTask addUsersToTask(Long taskId, List<Long> userIds) {
        PMSTask ret = null;
        
        ret = taskRepo.findById(taskId).orElseThrow(
                ()->new ResourceNotFoundException("No task found with id=" + taskId));
        
        List<Long> beRemovedUserIds = new ArrayList<>();
        // update task.userIds
        for (Long userId : userIds) {
        	if (userRepo.existsById(userId)) {
                ret.addUserId(userId);
                // as we have assigned the user to a certain task, 
                // we need to remove the user from the project default task. 
                if (ret.getProjectId() != PMSEntityConstants.kDefaultTaskProjectId) {
                    beRemovedUserIds.add(userId);
                }
            }
        }
        taskRepo.save(ret);
        removeUsersFromProject(ret.getProjectId(), beRemovedUserIds);
        
        return ret;
    }
    
    // return the default task
    public PMSTask addUsersToProject(Long projectId, List<Long> userIds) {
    	PMSProject project = projRepo.findById(projectId).orElseThrow(
    			()->new ResourceNotFoundException("No project found with id=" + projectId));
    	
    	PMSTask ret = addUsersToTask(project.getDefaultTask().getId(), userIds);
        
        return ret;
    }
    
    public PMSTask setUsersToTask(Long taskId, List<Long> userIds) {
        PMSTask ret = null;
        
        ret = taskRepo.findById(taskId).orElseThrow(
                ()->new ResourceNotFoundException("No task found with id=" + taskId));
        
        List<Long> beRemovedUserIds = new ArrayList<>();
        
        // clear task.userIds
        ret.getUserIds().clear();
        for (Long userId : userIds) {
            if (userRepo.existsById(userId)) {
                ret.addUserId(userId);
                // as we have assigned the user to a certain task, 
                // we need to remove the user from the project default task. 
                if (ret.getProjectId() != PMSEntityConstants.kDefaultTaskProjectId) {
                    beRemovedUserIds.add(userId);
                }
            } 
        }
        taskRepo.save(ret);
        removeUsersFromProject(ret.getProjectId(), beRemovedUserIds);
        
        return ret;
    }
    
    // return the default task
    public PMSTask setUsersToProject(Long projectId, List<Long> userIds) {
    	PMSProject project = projRepo.findById(projectId).orElseThrow(
    			()->new ResourceNotFoundException("No project found with id=" + projectId));
    	
    	List<Long> beAddedUserIds = new ArrayList<>();
    	for (Long userId : userIds) {
    	    if (!isUserExistsInProject(userId, projectId)) {
    	        beAddedUserIds.add(userId);
    	    }
    	}
    	
    	PMSTask ret = setUsersToTask(project.getDefaultTask().getId(), beAddedUserIds);
    	
    	return ret;
    }
    
    public PMSTask removeUsersFromTask(Long taskId, List<Long> userIds) {
        PMSTask task = taskRepo.findById(taskId).orElseThrow(
                ()-> {
                	log.debug("No task found with id=" + taskId);
                	return new ResourceNotFoundException("No task found with id=" + taskId);
                });
        
        List<Long> currentUserIds = task.getUserIds();
        for (Long userId : userIds) {
            if (currentUserIds.contains(userId)) {
                currentUserIds.remove(userId);
            }
        }
        taskRepo.save(task);
        
        return task;
    }
    
    // remove users from the project's default task. return the default task
    public PMSTask removeUsersFromProject(Long projectId, List<Long> userIds) {
    	PMSProject project = projRepo.findById(projectId).orElseThrow(
    			()->new ResourceNotFoundException("No project found with id=" + projectId));
    	
    	PMSTask ret = removeUsersFromTask(project.getDefaultTask().getId(), userIds);
        
        return ret;
    }
    
    public PMSUser updateUser(Long id, PMSUser user, Long companyId) {
    	PMSUser ret = userRepo.findById(id).orElseThrow(
                ()-> {
                	log.debug("No user found with id=" + id);
                	return new ResourceNotFoundException("No user found with id=" + id);
                });
        
        if (user.getFirstName() != null) {
        	ret.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
        	ret.setLastName(user.getLastName());
        }
        if (user.getEmail() != null) {
        	if (userRepo.existsByEmail(user.getEmail())) {
        		log.debug("cannot update user, user with email=" + user.getEmail() + " already exists.");
        		throw new DuplicateObjectsException("cannot update user, user with email=" + user.getEmail() + " already exists.");
        	} else {
        		ret.setEmail(user.getEmail());
        	}
        }
        if (user.getPassword() != null) {
        	ret.setPassword(passwdEncoder.encode(user.getPassword()));
        }
        
        if (user.getAvatar() != null) {
        	PMSFile avatar = ret.getAvatar();
        	if (avatar != null && avatar.getParentId().longValue() != PMSEntityConstants.kDefaultFileParentId) {
        		deleteFile(avatar.getId());
        	}
        	ret.setAvatar(user.getAvatar());
        }
        
        if (user.getRoles() != null) {
        	// process roles. we won't create new instance, instead we use existing roles. 
        	List<PMSRole> existingRoles = new ArrayList<>();
        	List<PMSRole> newRoles = user.getRoles();
        	for (PMSRole newRole : newRoles) {
        		PMSRole existingRole = this.getRoleByName(newRole.getName());
        		existingRoles.add(existingRole);
        	}
        	ret.setRoles(existingRoles);
        	// check if admin is included in new roles. 
        	if (existingRoles.contains(getRoleByName(PMSRoleName.admin))) {
        		// TODO
        		// if it is admin, remove this user from company
        	} else {
        		// TODO
        		// if it is not admin, add this user to companyId. 
        	}
        }

        return userRepo.save(ret);
    }
    
    // delete user, remove the user from project/task
    public void deleteUsers(List<Long> userIds) {
    	List<PMSProject> allProjects = projRepo.findAll();
    	for (PMSProject project : allProjects) {
    		// remove users from project's default task and other tasks. 
    		List<Long> taskIds = project.getTaskIds();
    		taskIds.add(project.getDefaultTask().getId());
    		for (Long taskId : taskIds) {
    			this.removeUsersFromTask(taskId, userIds);
    		}
    	}
    	
    	// delete user avatars. 
    	List<PMSFile> avatars = new ArrayList<>();
    	for (Long userId : userIds) {
    		// delete user's avatar.
    		avatars.clear();    		
    		PMSUser user = userRepo.findById(userId).orElse(null);
    		if (user != null) {
    			PMSFile avatar = user.getAvatar();
    			// it is a customized avatar. 
    			if (avatar != null &&
    					avatar.getParentId() != PMSEntityConstants.kDefaultFileParentId) {
    				deleteFile(avatar.getId());
    			}
    		}
    	}
    	
    	// remove users from company.
    	List<PMSCompany> companies = compRepo.findAll();
    	for (PMSCompany company : companies) {
    		boolean needSave = false;
    		List<Long> companyUserIds = company.getUserIds();
    		for (Long userId : userIds) {
    			if (companyUserIds.contains(userId)) {
    				companyUserIds.remove(userId);
    				needSave = true;
    			}
    		}
    		if (needSave) {
    			compRepo.save(company);
    		}
    	}
    	
    	// delete users. 
    	for (Long userId : userIds) {
    		userRepo.deleteById(userId);
    	}	
    }
    
    public List<PMSProject> getProjectsByUserId(Long userId) {
        userRepo.findById(userId).orElseThrow(
                ()-> {
                	log.debug("No user found with id=" + userId);
                	return new ResourceNotFoundException("No user found with id=" + userId);
                });
        
        List<PMSProject> ret = new ArrayList<>();
        
        List<PMSProject> allProjects = projRepo.findAll();
        for (PMSProject project : allProjects) {
            if (this.isUserExistsInProject(userId, project.getId())) {
                ret.add(project);
            }
        }
        
        return ret;
    }
    
    public List<List<PMSTask>> getTasksByUserId(Long userId) {
        List<List<PMSTask>> ret = new ArrayList<>();
        
        List<PMSProject> projects = getProjectsByUserId(userId);
        for (PMSProject project : projects) {
            List<PMSTask> item = getTasksByUserId(userId, project.getId());
            ret.add(item);
        }
        
        return ret;
    }

    public List<PMSTask> getTasksByUserId(Long userId, Long projectId) {
        PMSProject project = projRepo.findById(projectId).orElseThrow(
                ()-> {
                	log.debug("No project found with id=" + projectId);
                	return new ResourceNotFoundException("No project found with id=" + projectId);
                });
        if (!compRepo.existsById(project.getCompanyId())) {
        	log.debug("No company found with id=" + project.getCompanyId());
        	throw new ResourceNotFoundException("No company found with id=" + project.getCompanyId());
        }
        if (!userRepo.existsById(userId)) {
        	log.debug("No user found with id=" + userId);
        	throw new ResourceNotFoundException("No user found with id=" + userId);
        }
        
        List<PMSTask> ret = new ArrayList<>();
        List<PMSTask> allTasks = new ArrayList<>();
        // we don't add project to the user, as we want to find the tasks the user are working on. 
        //allTasks.add(project.getDefaultTask());
        List<PMSTask> tasks = getTasksByProjectIdAndCompanyId(project.getId(), project.getCompanyId());
        allTasks.addAll(tasks);
        
        for (PMSTask allTask : allTasks) {
            if (allTask.getUserIds().contains(userId)) {
                ret.add(allTask);
            }
        }
        
        return ret;
    }
    
    public PMSRole createRole(PMSRole role) {
    	PMSRole ret = null;
    	
    	ret = roleRepo.findByName(role.getName()).orElse(ret);
    	if (ret == null) {
    		roleRepo.save(role);
    	}
    	
    	return ret;
    }
    
    public List<PMSRole> getRoles() {
    	List<PMSRole> ret = roleRepo.findAll();
    	
    	return ret;
    }
    
    public PMSRole getRole(Long roleId) {
    	PMSRole ret = null;
    	
    	ret = roleRepo.findById(roleId).orElse(ret);
    	
    	return ret;
    }
    
    public PMSRole getRole(PMSRoleName name) {
		PMSRole ret = null;
		
		ret = roleRepo.findByName(name).orElse(ret);
		
		return ret;
	}

	public PMSRole updateRole(Long roleId, PMSRole role) {
		if (roleId.longValue() != role.getId().longValue()) {
			throw new RequestValueMismatchException();
		}
		
		PMSRole ret = getRole(roleId);
		if (ret == null) {
			throw new ResourceNotFoundException("No role found with id=" + roleId);
		}
		
		/*if (role.getName() != null) {
			ret.setName(role.getName());
		}*/
		
		if (role.getDesc() != null) {
			ret.setDesc(role.getDesc());
		}
		
		roleRepo.save(ret);
		
		return ret;
	}
	
	public PMSRole updateRole(PMSRoleName roleName, PMSRole role) {
		PMSRole ret = null;
		
		ret = getRole(roleName);
		if (ret == null) {
			throw new ResourceNotFoundException("No role found with name=" + roleName);
		}
		
		if (role.getDesc() != null) {
			ret.setDesc(role.getDesc());
		}
		
		roleRepo.save(ret);
		
		return ret;
	}

	public void deleteRole(Long roleId) {
		// TODO
	}
	
	public void deleteRole(PMSRoleName name) {
		// TODO
	}

	public List<PMSUser> getUsersByRoleId(Long roleId) {
		List<PMSUser> ret = new ArrayList<>();
		
		List<PMSUser> users = userRepo.findAll();
		for (PMSUser user : users) {
			boolean found = false;
			List<PMSRole> roles = user.getRoles();
			for (PMSRole role : roles) {
				if (role.getId().longValue() == roleId.longValue()) {
					found = true;
					break;
				}
			}
			if (found) {
				ret.add(user);
			}
		}
		
		return ret;
	}

	public List<PMSUser> getUsersByRoleName(PMSRoleName roleName) {
		List<PMSUser> ret = new ArrayList<>();
		
		List<PMSUser> users = userRepo.findAll();
		for (PMSUser user : users) {
			boolean found = false;
			List<PMSRole> roles = user.getRoles();
			for (PMSRole role : roles) {
				if (role.getName() == roleName) {
					found = true;
					break;
				}
			}
			if (found) {
				ret.add(user);
			}
		}
		
		return ret;
	}
	
	public List<PMSTag> updateTags(List<PMSTag> tags) {
		List<PMSTag> ret = null;
		
		List<PMSTag> allTags = tagRepo.findAll();

		for (PMSTag tag : tags) {
			if (!allTags.contains(tag)) {
				allTags.add(tag);
			}
		}
		
		tagRepo.saveAll(tags);
		ret = tags;
		
		return ret;
	}
	
	public void deleteFile(Long fileId) {
		if (fileRepo.existsById(fileId)) {
			PMSFile file = fileRepo.findById(fileId).orElseGet(null);
			if (file != null) {
				String path = file.getRealFilename();
				fileRepo.deleteById(fileId);
				// remove file from system. 
				File beDeleted = new File(path);
			    beDeleted.delete();
			}
		} else {
			throw new ResourceNotFoundException("File cannot be found with id=" + fileId);
		}
	}
	
	public PMSProject startProject(Long projectId) {
		return null;
	}
	
	public PMSProject stopProject(Long projectId) {
		return null;
	}
	
	public PMSTask startTask(Long taskId) {
		return null;
	}
	
	public PMSTask stopTask(Long taskId) {
		return null;
	}
	
	// file upload
	public PMSFile upload(MultipartFile file, String displayName, PMSFileType fileType) {
		
	    // init directories
		String strUploadDir = ClassUtils.getDefaultClassLoader().getResource("").getPath();
		strUploadDir += "../../upload";
	    File uploadDir = new File(strUploadDir);
	    if (!uploadDir.exists()) {
	    	uploadDir.mkdirs();
	    }
	    
	    // create avatars and files if neccessary. 
	    String strAvatarDir = strUploadDir + "/avatars";
	    File avatarDir = new File(strAvatarDir);
	    if (!avatarDir.exists()) {
	    	avatarDir.mkdirs();
	    }
	    String strFileDir = strUploadDir + "/files";
	    File fileDir = new File(strFileDir);
	    if (!fileDir.exists()) {
	    	fileDir.mkdirs();
	    }
	    
	    // use client's file name as display name if display name is not specified. 
	    String filename = null;
	    if (displayName != null) {
	    	filename = displayName;
	    } else {
	    	filename = file.getOriginalFilename();
	    }
	    long size = file.getSize();
	    
	    // create & save entity.
	    PMSFile entityFile = new PMSFile();
	    entityFile.setFileType(fileType);
	    entityFile.setDisplayFilename(filename);
	    entityFile.setSize(size);
	    entityFile.setRealFilename("" + System.currentTimeMillis());
	    
	    String str = null;
	    if (fileType == PMSFileType.Image) {
	    	str = avatarDir.getAbsolutePath() + File.separator + entityFile.getRealFilename();
	    } else {
	    	str = fileDir.getAbsolutePath() + File.separator + entityFile.getRealFilename();
	    }

	    try {
		    file.transferTo(new File(str));
		    entityFile = fileRepo.save(entityFile);
	    } catch (Exception e) {
	    	// TODO 
	    }
	    
	    return entityFile;
	}
}
