import org.activiti.engine.*;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.List;

public class ProcessTest {


/*
*完整流程测试
* 1、加载流程定义 activiti.cfg3.xml
* 2、部署流程
* 3、启动流程 financialReport
* 4、根据流程定义的分组获取任务列表
* 5、将流程分配给指定人员
* 6、为该任务设置变量
* 7、完成该任务并设置审批意见
* 8、包含网关将会根据变量结合路线中的判定条件选择下一步的任务
* 9、获取下一步任务处理人或所属分组
* 10、完成所有符合条件的任务，测试包含网关会自动忽略不符合条件的task，认定该流程结束。
* 11、查询该流程所有审批记录
* */
    @Test
    public void test1(){
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg3.xml");
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();
        HistoryService historyService = processEngine.getHistoryService();

        DeploymentBuilder deployment = repositoryService.createDeployment();
        deployment.addClasspathResource("test3.bpmn");

        ProcessInstance financialReport = runtimeService.startProcessInstanceByKey("financialReport");

        final String processInstId = financialReport.getId();

        //根据流程实例id获取当前流程任务
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstId).list();

        //获取某一分组下的所有任务
        List<Task> accountancyTask = taskService.createTaskQuery().taskCandidateGroup("accountancy").list();

        for (Task task :tasks){
            taskService.claim(task.getId(),"朱米盛");

        }

        //获取某一用户所有任务
        List<Task> zhumishengTask = taskService.createTaskQuery().taskAssignee("朱米盛").list();

        for (Task task :tasks){
            taskService.setVariable(task.getId(),"result","pass");
        }

        //不直接编码完成该该任务，尝试跳过该任务

        for (Task task :tasks) {
            ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(task.getProcessDefinitionId());



            List<Task> subTasks = taskService.getSubTasks(task.getId());

            for (Task task1:subTasks){

            }



        }


    }
}
