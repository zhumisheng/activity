import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class Demo {

    private ProcessEngine processEngine;

    @Test
    public void test()  {
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        DeploymentBuilder deployment = repositoryService.createDeployment();
        deployment.addClasspathResource("test.bpmn").deploy();
        runtimeService.startProcessInstanceByKey("financialReport");

        TaskService taskService = processEngine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<Task> tasks = taskQuery.taskCandidateGroup("accountancy").list();
        System.out.println(tasks.size());
        for (Task task:tasks
             ) {
            System.out.println(task.getName());
            System.out.println(task.getId());
            taskService.claim(task.getId(),"zhumisheng");
        }
        List<Task> zhumisheng = taskQuery.taskAssignee("zhumisheng").list();
        System.out.println("执行前朱米盛任务数量"+zhumisheng.size());
        for (Task task:tasks
                ) {
            taskService.complete(task.getId());
        }

        System.out.println("执行后朱米盛任务数量"+zhumisheng.size());
        System.out.println(tasks.size());


    }

    @Test
    public void test1() {

        //加载activiti配置文件
                ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //加载面试流程定义并部署流程
        repositoryService.createDeployment().addClasspathResource("Interview.bpmn").deploy();
        //开启面试流程
        String processId = runtimeService.startProcessInstanceByKey("Interview").getId();

        System.out.println("流程编号:"+processId);

        TaskService taskService = processEngine.getTaskService();
        System.out.println("\n************笔试流程开始***************");

        //获取任务查询对象
        TaskQuery taskQuery = taskService.createTaskQuery();
        //奖该对象与部门绑定
        taskQuery.taskCandidateGroup("人力资源部");
        //获取改部门下所有的任务列表
        List<Task> list = taskQuery.list();
        //根据流程实例号和任务定义符号查询对应工作人员
        //将该部门所有的任务绑定给指定人员
        for (Task task :list){
            String id = task.getId();
            System.out.println(id);
            taskService.claim(id,"zhumisheng");
        }
        //根据流程实例号和任务定义符号查询对应工作人员
        List<Task> bishi = taskService.createTaskQuery().processInstanceId("4").taskDefinitionKey("bishi").list();
        System.out.println("根据流程实例id和任务定义关键字查询出的任务列表长度为"+bishi.size());
        List<Task> list2 =new ArrayList<Task>();
                list2 = taskService.createTaskQuery().processDefinitionKey("financialReport").taskDefinitionKey("bishi").taskAssignee("zhumisheng").list();
        System.out.println("根据流程定义key和任务可以加候选人名字查询出任务列表长度为"+list2.size());
        //新建查询对象
        TaskQuery taskQuery1 = taskService.createTaskQuery();
        taskQuery1.taskAssignee("zhumisheng");
        System.out.println( taskQuery1.count());
        List<Task> list1 = taskQuery1.list();
        for (Task task :list1){
            System.out.println("task名称："+task.getName());
            System.out.println("taskId"+task.getId());
            //完成该项任务
            taskService.complete(task.getId());
            System.out.println( taskQuery1.count());
        }


        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateGroup("人力资源部").list();
        System.out.println("张三的任务数量："+taskService.createTaskQuery().taskAssignee("张三").count());
        for (Task task : tasks) {
            System.out.println("人力资源部的任务：name:"+task.getName()+",id:"+task.getId());
            taskService.claim(task.getId(), "张三");
        }


        tasks = taskService.createTaskQuery().taskAssignee("张三").list();
        for (Task task : tasks) {
            System.out.println("张三的任务：name:"+task.getName()+",id:"+task.getId());
            taskService.complete(task.getId());
        }

        System.out.println("张三的任务数量："+taskService.createTaskQuery().taskAssignee("张三").count());
        System.out.println("***************笔试流程结束***************");

        System.out.println("\n***************一面流程开始***************");
        //笔试流程结束后相关流程会自动转到技术部
        tasks = taskService.createTaskQuery().taskCandidateGroup("技术部").list();
        for (Task task : tasks) {
            System.out.println("技术部的任务：name:"+task.getName()+",id:"+task.getId());
            //将每一个任务和技术部的李四绑定
            taskService.claim(task.getId(), "李四");
        }

        System.out.println("李四的任务数量："+taskService.createTaskQuery().taskAssignee("李四").count());
        for (Task task : tasks) {
            System.out.println("李四的任务：name:"+task.getName()+",id:"+task.getId());
            //李四完成每一项任务
            taskService.complete(task.getId());
        }

        System.out.println("李四的任务数量："+taskService.createTaskQuery().taskAssignee("李四").count());
        System.out.println("***************一面流程结束***************");

        System.out.println("\n***************二面流程开始***************");
        tasks = taskService.createTaskQuery().taskCandidateGroup("技术部").list();
        for (Task task : tasks) {
            System.out.println("技术部的任务：name:"+task.getName()+",id:"+task.getId());
            taskService.claim(task.getId(), "李四");
        }

        System.out.println("李四的任务数量："+taskService.createTaskQuery().taskAssignee("李四").count());
        for (Task task : tasks) {
            System.out.println("李四的任务：name:"+task.getName()+",id:"+task.getId());
            taskService.complete(task.getId());
        }

        System.out.println("李四的任务数量："+taskService.createTaskQuery().taskAssignee("李四").count());
        System.out.println("***************二面流程结束***************");

        System.out.println("***************HR面流程开始***************");
        tasks = taskService.createTaskQuery().taskCandidateGroup("人力资源部").list();
        for (Task task : tasks) {
            System.out.println("人力资源部的任务：name:"+task.getName()+",id:"+task.getId());
            taskService.claim(task.getId(), "王五");
        }

        System.out.println("王五的任务数量："+taskService.createTaskQuery().taskAssignee("王五").count());
        for (Task task : tasks) {
            System.out.println("王五的任务：name:"+task.getName()+",id:"+task.getId());
            taskService.complete(task.getId());
        }

        System.out.println("王五的任务数量："+taskService.createTaskQuery().taskAssignee("王五").count());
        System.out.println("***************HR面流程结束***************");

        System.out.println("\n***************录用流程开始***************");
        tasks = taskService.createTaskQuery().taskCandidateGroup("人力资源部").list();
        for (Task task : tasks) {
            System.out.println("人力资源部：name:"+task.getName()+",id:"+task.getId());
            taskService.claim(task.getId(), "王五");
        }

        System.out.println("王五的任务数量："+taskService.createTaskQuery().taskAssignee("王五").count());
        for (Task task : tasks) {
            System.out.println("王五的任务：name:"+task.getName()+",id:"+task.getId());
            taskService.complete(task.getId());
        }

        System.out.println("王五的任务数量："+taskService.createTaskQuery().taskAssignee("王五").count());
        System.out.println("***************录用流程结束***************");

        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processId).singleResult();
        System.out.println("\n流程结束时间："+historicProcessInstance.getEndTime());
    }
    @Test
    public void test2() {

        //加载activiti配置文件
                ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg2.xml").buildProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //加载面试流程定义并部署流程
        repositoryService.createDeployment().addClasspathResource("test.bpmn").deploy();
        //开启流程
        String processId = runtimeService.startProcessInstanceByKey("financialReport").getId();

        System.out.println("流程编号:"+processId);

        TaskService taskService = processEngine.getTaskService();
        System.out.println("\n************报告流程开始***************");

        //新建查询对象
        TaskQuery taskQuery = taskService.createTaskQuery().taskCandidateGroup("accountancy");
        System.out.println( taskQuery.count());
        List<Task> list = taskQuery.list();
        for (Task task :list){
            System.out.println("task名称："+task.getName());
            System.out.println("taskId"+task.getId());
            System.out.println( "绑定前该小组任务数量："+taskQuery.count());
            taskService.claim(task.getId(),"朱米盛");
            System.out.println( "绑定后该小组任务数量："+taskQuery.count());
        }

        TaskQuery taskQuery1 = taskService.createTaskQuery().taskAssignee("朱米盛");
        System.out.println("朱米盛任务数量："+taskQuery1.count());
        List<Task> list1 = taskQuery1.list();
        for (Task task:list1
             ) {
            taskService.complete(task.getId());
        }
        System.out.println("朱米盛执行后任务数量"+list1.size());


        System.out.println("***************写报告流程结束***************");

        System.out.println("\n***************一面流程开始***************");
        //笔试流程结束后相关流程会自动转到技术部
        TaskQuery taskQuery2 = taskService.createTaskQuery().taskCandidateGroup("management");
        System.out.println(taskQuery2.count());
        List<Task> tasks = taskQuery2.list();
        for (Task task : tasks) {
            System.out.println("management的任务：name:"+task.getName()+",id:"+task.getId());
            //将每一个任务和技术部的李四绑定
            taskService.claim(task.getId(), "李四");
        }

        System.out.println("李四的任务数量："+taskService.createTaskQuery().taskAssignee("李四").count());
        for (Task task : tasks) {
            System.out.println("李四的任务：name:"+task.getName()+",id:"+task.getId());
            //李四完成每一项任务
            taskService.complete(task.getId());
        }

        System.out.println("李四的任务数量："+taskService.createTaskQuery().taskAssignee("李四").count());
        System.out.println("***************报告审阅结束***************");

    }
    @Test
    public void test3() {

        //加载activiti配置文件
                ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg2.xml").buildProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //加载面试流程定义并部署流程
        repositoryService.createDeployment().addClasspathResource("test.bpmn").deploy();
        //开启流程
        String processId = runtimeService.startProcessInstanceByKey("financialReport").getId();

        System.out.println("流程编号:"+processId);

        TaskService taskService = processEngine.getTaskService();
        System.out.println("\n************报告流程开始***************");

        //新建查询对象
        TaskQuery taskQuery = taskService.createTaskQuery().taskCandidateGroup("accountancy");
        System.out.println( taskQuery.count());
        List<Task> list = taskQuery.list();
        for (Task task :list){
            System.out.println("task名称："+task.getName());
            System.out.println("taskId"+task.getId());
            System.out.println( "绑定前该小组任务数量："+taskQuery.count());
            taskService.claim(task.getId(),"朱米盛");
            System.out.println( "绑定后该小组任务数量："+taskQuery.count());
        }

        TaskQuery taskQuery1 = taskService.createTaskQuery().taskAssignee("朱米盛");
        System.out.println("朱米盛任务数量："+taskQuery1.count());
        List<Task> list1 = taskQuery1.list();
        for (Task task:list1
             ) {
//            为流程设置变量，以供condition表达式进行条件判断
            runtimeService.setVariable(task.getExecutionId(),"result","failed");
            taskService.complete(task.getId());
            System.out.println(task.getExecutionId());

        }
        System.out.println("朱米盛执行后任务数量"+list1.size());


        System.out.println("***************写报告流程结束***************");

        System.out.println("\n***************审阅流程开始***************");
        //笔试流程结束后根据resylt变量判断跳转目的地，如果流程的resulet变量为pass则进入managent
        TaskQuery taskQuery2 = taskService.createTaskQuery().taskCandidateGroup("management");
        System.out.println(taskQuery2.count());
        List<Task> tasks = taskQuery2.list();
        for (Task task : tasks) {
            System.out.println("management的任务：name:"+task.getName()+",id:"+task.getId());
            //将每一个任务和技术部的李四绑定
            taskService.claim(task.getId(), "李四");
        }

        System.out.println("李四的任务数量："+taskService.createTaskQuery().taskAssignee("李四").count());
        for (Task task : tasks) {
            System.out.println("李四的任务：name:"+task.getName()+",id:"+task.getId());
            //李四完成每一项任务
            taskService.complete(task.getId());
        }

        System.out.println("李四的任务数量："+taskService.createTaskQuery().taskAssignee("李四").count());
        System.out.println("***************报告审阅结束***************");
        //笔试流程结束后根据resylt变量判断跳转目的地，如果流程的faild变量为pass则进入boss
        TaskQuery taskQuery3 = taskService.createTaskQuery().taskCandidateGroup("boss");
        System.out.println(taskQuery3.count());
        List<Task> bossTasks = taskQuery3.list();
        for (Task task : bossTasks) {
            System.out.println("management的任务：name:"+task.getName()+",id:"+task.getId());
            //将每一个任务和技术部的李四绑定
            taskService.claim(task.getId(), "王五");
        }

        System.out.println("王五的任务数量："+taskService.createTaskQuery().taskAssignee("王五").count());
        for (Task task : bossTasks) {
            System.out.println("王五的任务：name:"+task.getName()+",id:"+task.getId());
            //李四完成每一项任务
            taskService.complete(task.getId());
        }

        System.out.println("王五的任务数量："+taskService.createTaskQuery().taskAssignee("王五").count());
        System.out.println("***************报告审阅结束***************");

    }

    /*
    * 测试并行网关
    * */
    @Test
    public void test4() {

        //加载activiti配置文件
                ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg2.xml").buildProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //加载面试流程定义并部署流程
        repositoryService.createDeployment().addClasspathResource("test2.bpmn").deploy();
        //开启流程
        String processId = runtimeService.startProcessInstanceByKey("financialReport").getId();

        System.out.println("流程编号:"+processId);

        TaskService taskService = processEngine.getTaskService();
        System.out.println("\n************报告流程开始***************");

        //新建查询对象
        TaskQuery taskQuery = taskService.createTaskQuery().taskCandidateGroup("accountancy");
        System.out.println( taskQuery.count());
        List<Task> list = taskQuery.list();
        for (Task task :list){
            System.out.println("task名称："+task.getName());
            System.out.println("taskId"+task.getId());
            System.out.println( "绑定前该小组任务数量："+taskQuery.count());
            taskService.claim(task.getId(),"朱米盛");
            System.out.println( "绑定后该小组任务数量："+taskQuery.count());
        }

        TaskQuery taskQuery1 = taskService.createTaskQuery().taskAssignee("朱米盛");
        System.out.println("朱米盛任务数量："+taskQuery1.count());
        List<Task> list1 = taskQuery1.list();
        for (Task task:list1
             ) {
            taskService.complete(task.getId());
            System.out.println(task.getExecutionId());

        }
        System.out.println("朱米盛执行后任务数量"+list1.size());


        System.out.println("***************写报告流程结束***************");

        System.out.println("\n***************审阅流程开始***************");
        //笔试流程结束后同时 跳转到managent和boss
        TaskQuery taskQuery2 = taskService.createTaskQuery().taskCandidateGroup("management");
        System.out.println(taskQuery2.count());
        List<Task> tasks = taskQuery2.list();
        for (Task task : tasks) {
            System.out.println("management的任务：name:"+task.getName()+",id:"+task.getId());
            //将每一个任务和技术部的李四绑定
            taskService.claim(task.getId(), "李四");
        }

        System.out.println("李四的任务数量："+taskService.createTaskQuery().taskAssignee("李四").count());
        for (Task task : tasks) {
            System.out.println("李四的任务：name:"+task.getName()+",id:"+task.getId());
            //李四完成每一项任务
            taskService.complete(task.getId());
        }

        System.out.println("李四的任务数量："+taskService.createTaskQuery().taskAssignee("李四").count());
        System.out.println("***************报告审阅结束***************");
        //笔试流程结束后根据resylt变量判断跳转目的地，如果流程的faild变量为pass则进入boss
        TaskQuery taskQuery3 = taskService.createTaskQuery().taskCandidateGroup("boss");
        System.out.println(taskQuery3.count());
        List<Task> bossTasks = taskQuery3.list();
        for (Task task : bossTasks) {
            System.out.println("management的任务：name:"+task.getName()+",id:"+task.getId());
            //将每一个任务和技术部的李四绑定
            taskService.claim(task.getId(), "王五");
        }

        System.out.println("王五的任务数量："+taskService.createTaskQuery().taskAssignee("王五").count());
        for (Task task : bossTasks) {
            System.out.println("王五的任务：name:"+task.getName()+",id:"+task.getId());
            //李四完成每一项任务
            taskService.complete(task.getId());
        }

        System.out.println("王五的任务数量："+taskService.createTaskQuery().taskAssignee("王五").count());
        System.out.println("***************报告审阅结束***************");


        HistoryService historyService = processEngine.getHistoryService();
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
    }
    /*
    * 测试包含网关
    * */
    @Test
    public void test5() {

        //加载activiti配置文件
                ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg3.xml").buildProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //加载面试流程定义并部署流程
        repositoryService.createDeployment().addClasspathResource("test3.bpmn").deploy();
        //开启流程
        String processId = runtimeService.startProcessInstanceByKey("financialReport").getId();
        String processId2 = runtimeService.startProcessInstanceByKey("financialReport").getId();
        String processId3 = runtimeService.startProcessInstanceByKey("financialReport").getId();
        String processId4 = runtimeService.startProcessInstanceByKey("financialReport").getId();
        String processId5 = runtimeService.startProcessInstanceByKey("financialReport").getId();
        String processId6 = runtimeService.startProcessInstanceByKey("financialReport").getId();
        String processId7 = runtimeService.startProcessInstanceByKey("financialReport").getId();

        System.out.println("流程编号:"+processId);
        System.out.println("流程编号2:"+processId2);
        System.out.println("流程编号3:"+processId3);
        System.out.println("流程编号4:"+processId4);
        System.out.println("流程编号5:"+processId5);
        System.out.println("流程编号6:"+processId6);
        System.out.println("流程编号7:"+processId7);

        TaskService taskService = processEngine.getTaskService();
        System.out.println("\n************报告流程开始***************");

        //新建查询对象
        TaskQuery taskQuery = taskService.createTaskQuery().taskCandidateGroup("accountancy");
        System.out.println( taskQuery.count());
        List<Task> list = taskQuery.list();
        for (Task task :list){
            System.out.println("task名称："+task.getName());
            System.out.println("taskId"+task.getId());
            System.out.println( "绑定前该小组任务数量："+taskQuery.count());
            taskService.claim(task.getId(),"朱米盛");
            task.setAssignee("zhumisheng");
            System.out.println( "绑定后该小组任务数量："+taskQuery.count());
        }

        List<Task> list2 =new ArrayList<Task>();
        list2 = taskService.createTaskQuery().processDefinitionKey("financialReport").taskDefinitionKey("writeReportTask").taskAssignee("朱米盛").list();

        System.out.println("根据流程定义key和任务定义key加候选人名字查询出任务列表长度为"+list2.size());

        for (Task task:list2
                ) {
            String processInstanceId = task.getProcessInstanceId();
            List<Task> list1 = taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey("writeReportTask").taskAssignee("朱米盛").list();
            System.out.println(list1.get(0).getExecutionId());
        }
        TaskQuery taskQuery1 = taskService.createTaskQuery().taskAssignee("朱米盛");
        System.out.println("朱米盛任务数量："+taskQuery1.count());
        List<Task> list1 = taskQuery1.list();
        for (Task task:list1
             ) {
            runtimeService.setVariable(task.getExecutionId(),"result","pass");
            taskService.complete(task.getId());
            System.out.println("执行Id"+task.getExecutionId());
        }
        System.out.println("朱米盛执行后任务数量"+taskQuery1.count());

        System.out.println("***************写报告流程结束***************");

        System.out.println("\n***************审阅流程开始***************");
        //笔试流程结束后同时 跳转到managent和boss或ceo
        TaskQuery taskQuery2 = taskService.createTaskQuery().taskCandidateGroup("management");
        System.out.println(taskQuery2.count());
        List<Task> tasks = taskQuery2.list();
        for (Task task : tasks) {
            System.out.println("management的任务：name:"+task.getName()+",id:"+task.getId());
            //将每一个任务和李四绑定
            taskService.claim(task.getId(), "李四");
        }

        System.out.println("李四的任务数量："+taskService.createTaskQuery().taskAssignee("李四").count());
        for (Task task : tasks) {
            System.out.println("李四的任务：name:"+task.getName()+",id:"+task.getId());
            //李四完成每一项任务
            taskService.complete(task.getId());
        }

        System.out.println("李四的任务数量："+taskService.createTaskQuery().taskAssignee("李四").count());
        System.out.println("***************报告审阅结束***************");
        //笔试流程结束后根据resylt变量判断跳转目的地，如果流程的faild变量为pass则进入boss
        TaskQuery taskQuery3 = taskService.createTaskQuery().taskCandidateGroup("boss");
        System.out.println(taskQuery3.count());
        List<Task> bossTasks = taskQuery3.list();
        for (Task task : bossTasks) {
            System.out.println("management的任务：name:"+task.getName()+",id:"+task.getId());
            System.out.println("任务Id"+task.getExecutionId());
            //将每一个任务和技术部的王五绑定
            taskService.claim(task.getId(), "王五");
        }

        System.out.println("王五的任务数量："+taskService.createTaskQuery().taskAssignee("王五").count());
        for (Task task : bossTasks) {
            System.out.println("王五的任务：name:"+task.getName()+",id:"+task.getId());
            //李四完成每一项任务
            taskService.complete(task.getId());
        }

        System.out.println("王五的任务数量："+taskService.createTaskQuery().taskAssignee("王五").count());
        System.out.println("***************报告审阅结束***************");
//        张三从CEO分组去任务，而ceo和boss分组条件相反，所以boss分组和ceo分组不能同时取得任务
        TaskQuery taskQuery4 = taskService.createTaskQuery().taskCandidateGroup("CEO");
        System.out.println(taskQuery3.count());
        List<Task> CEOTasks = taskQuery3.list();
        for (Task task : CEOTasks) {
            System.out.println("management的任务：name:"+task.getName()+",id:"+task.getId());
            //将每一个CEO任务和张三绑定
            taskService.claim(task.getId(), "张三");
        }

        System.out.println("张三的任务数量："+taskService.createTaskQuery().taskAssignee("张三").count());
        for (Task task : CEOTasks) {
            System.out.println("张三的任务：name:"+task.getName()+",id:"+task.getId());
            //李四完成每一项任务
//            taskService.complete(task.getId());
        }

        System.out.println("张三的任务数量："+taskService.createTaskQuery().taskAssignee("张三").count());
        System.out.println("***************报告审阅结束***************");

        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        HistoricProcessInstanceQuery historicProcessInstanceQuery1 = historicProcessInstanceQuery.processInstanceId("4");
        List<HistoricProcessInstance> list6 = historicProcessInstanceQuery.list();
        for (HistoricProcessInstance processInstance :list6){
            System.out.println("一个流程实例");
            System.out.println("流程实例结束时间："+processInstance.getEndTime());
            System.out.println("流程开始用户id："+processInstance.getStartUserId());
            System.out.println("流程最后一个活动id："+processInstance.getEndActivityId());
            System.out.println("流程结束时间："+processInstance.getEndTime());
            System.out.println("流程定义的key"+processInstance.getProcessDefinitionKey());
            System.out.println(processInstance.getDeploymentId());
            System.out.println(processInstance.getDescription());
        }
        HistoricProcessInstanceQuery historicProcessInstanceQuery3 = historicProcessInstanceQuery.processInstanceId("4");
        List<HistoricProcessInstance> list4 = historicProcessInstanceQuery.list();
        for (HistoricProcessInstance processInstance :list4){
            System.out.println("一个流程实例");
            System.out.println("流程实例结束时间："+processInstance.getEndTime());
            System.out.println("流程开始用户id："+processInstance.getStartUserId());
            System.out.println("流程最后一个活动id："+processInstance.getEndActivityId());
            System.out.println("流程结束时间："+processInstance.getEndTime());
            System.out.println("流程定义的key"+processInstance.getProcessDefinitionKey());
            System.out.println(processInstance.getDeploymentId());
            System.out.println(processInstance.getDescription());
        }
        HistoricProcessInstanceQuery historicProcessInstanceQuery2 = historicProcessInstanceQuery.processInstanceId("10");
        List<HistoricProcessInstance> list3 = historicProcessInstanceQuery2.list();
        for (HistoricProcessInstance processInstance :list3){
            System.out.println("一个流程实例");
            System.out.println("流程实例结束时间："+processInstance.getEndTime());
            System.out.println("流程开始用户id："+processInstance.getStartUserId());
            System.out.println("流程最后一个活动id："+processInstance.getEndActivityId());
            System.out.println("流程结束时间："+processInstance.getEndTime());
            System.out.println("流程定义的key"+processInstance.getProcessDefinitionKey());
            System.out.println(processInstance.getDeploymentId());
            System.out.println(processInstance.getDescription());
        }
    }
    @Test
    public void test6(){
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg2.xml").buildProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        HistoricProcessInstanceQuery historicProcessInstanceQuery1 = historicProcessInstanceQuery.processInstanceId("4");
        List<HistoricProcessInstance> list = historicProcessInstanceQuery.list();
        System.out.println("开始打印");
        for (HistoricProcessInstance processInstance :list){
            System.out.println(processInstance.getDeploymentId());
            System.out.println(processInstance.getDescription());
            System.out.println("一个过程实例对象");
            ComboPooledDataSource dataSource = new ComboPooledDataSource();
        }
    }

}
