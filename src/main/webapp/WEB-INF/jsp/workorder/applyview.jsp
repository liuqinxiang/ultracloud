<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
<base href="<%=basePath%>">
<!-- jsp文件头和头部 -->
<%@ include file="../system/index/top.jsp"%>
</head>
<body class="no-skin">

						<div class="page-header">
						
								<button class="btn btn-success" id="cancel" name="cancel" type="reset" onclick="top.Dialog.close();">
									返回
								</button>
								<small>
								<button class="btn disabled btn-success">工单号：${workorder.appNo}</button>
								</small>
							
						</div><!-- /.page-header -->
						<div class="alert alert-info">流程图</div>
						<p></p>
						
						<div>
						<img id="procImg" src="<c:url value='findworkflowPic.do?procDefId=${procDefId}' />" />  
						<c:if test="${workorder.appNo != '5'}">
							<!-- 给执行的节点加框 --> 
				       	 	<div style="position: absolute; border:2px solid red;left:${workorderImag.x}px;top:${workorderImag.y + 153}px;width:${workorderImag.width }px;height:${workorderImag.height }px;"></div> 
						</c:if>
						</div>
						
						<div>
						<table id="simple-table"
								class="table table-striped table-bordered table-hover"
								style="margin-top: 5px;">
								<thead>
									<tr>
										<th class="center">项目名称</th>
										<th class="center">项目等级</th>
										<th class="center">资源申请标准</th>
										<th class="center">项目已用资源(cpu/内存/存储)</th>
										<th class="center">项目剩余资源(cpu/内存/存储)</th>
										<th class="center">负责人</th>
									</tr>
								</thead>

								<tbody>
												<tr>
													<td class='center'>${project.name}</td>
													<td class='center'>${project.level}</td>
													<td class='center'>
														<c:if test="${(project.cpu_quota-project.cpu_used) > 0 && (project.memory_quota-project.memory_used) >0 &&(project.disk_quota-project.disk_used) > 0}">符合</c:if>
														<c:if test="${(project.cpu_quota-project.cpu_used) <= 0 || (project.memory_quota-project.memory_used) <= 0 || (project.disk_quota-project.disk_used) <= 0}">不符合</c:if>
													</td>
													<td class='center'>${project.cpu_used}/${project.memory_used}/${project.disk_used}</td>
													<td class='center'>${project.cpu_quota-project.cpu_used}/${project.memory_quota-project.memory_used}/${project.disk_quota-project.disk_used}</td>
													<td class='center'>${project.USERNAME}</td>
													</tr>
								</tbody>
						</table>
						</div>
						<h1></h1>
						
						<div class="alert alert-info">服务目录</div>
						<div>
						<table id="simple-table"
								class="table table-striped table-bordered table-hover"
								style="margin-top: 5px;">
								<tbody>
									<thead>
										<tr>
											<th class='center'>云资源申请总览</th>
										</tr>
									</thead>
									
									<tr>
										<td class='center' style="background-color: rgb(244,244,244);">申请云主机数量</td>
										<td class='center'>${cloudInfoCollect.vmCount}台</td>
										<td class='center' style="background-color: rgb(244,244,244);">cpu/内存总量</td>
										<td class='center'>${cloudInfoCollect.cpuTotal}核/${cloudInfoCollect.memoryTotal}G</td>
										<td class='center' style="background-color: rgb(244,244,244);">数据盘总量</td>
										<td class='center'>${cloudInfoCollect.diskTotal}G</td>
									</tr>
								</tbody>
						</table>
						<h2></h2>
						<table id="simple-table"
								class="table table-striped table-bordered table-hover"
								style="margin-top: 5px;">
								<tbody>
									<thead>
										<tr>
											<th class='center'>申请详情</th>
										</tr>
									</thead>
									<tr>
										<td class='center' style="background-color: rgb(244,244,244);">操作系统</td>
										<td class='center'>${cmpCloudInfo.osTypeName}</td>
										<td class='center' style="background-color: rgb(244,244,244);">系统盘/数据盘</td>
										<td class='center'>系统盘${cmpCloudInfo.sysDiskSize}; 数据盘${cloudInfoCollect.diskTotal}G</td>
									</tr>
									<tr>
										<td class='center' style="background-color: rgb(244,244,244);">CPU/内存</td>
										<td class='center'>${cmpCloudInfo.cpu}核/${cmpCloudInfo.memory}G</td>
										<td class='center' style="background-color: rgb(244,244,244);">安装状态</td>
										<td class='center'>${cmpCloudInfo.osStatus}</td>
									</tr>
									<tr>
										<td class='center' style="background-color: rgb(244,244,244);">软件安装</td>
										<td class='center'>${cmpCloudInfo.softStatus}</td>
									</tr>
									<tr>
										<td class='center' style="background-color: rgb(244,244,244);">数据盘信息</td>
										<td class='center'>${cmpCloudInfo.dataDiskInfo}</td>
										<td class='center' style="background-color: rgb(244,244,244);">使用期限</td>
										<td class='center'>${cmpCloudInfo.expireDate}</td>
									</tr>
									<tr>
										<td class='center' style="background-color: rgb(244,244,244);">电子钥匙</td>
										<td class='center'></td>
									</tr>
								</tbody>
						</table>
				        </div>
						<div class="alert alert-info">关联任务</div>
						<div>
						<table id="simple-table"
								class="table table-striped table-bordered table-hover"
								style="margin-top: 5px;">
								<thead>
									<tr>
										<th class="center">任务单号</th>
										<th class="center">任务处理人</th>
										<th class="center">任务状态</th>
										<th class="center">处理结果</th>
										<th class="center">处理时间</th>
									</tr>
								</thead>

								<tbody>
									<!-- 开始循环 -->
									<c:choose>
										<c:when test="${not empty relateTaskList}">
											<c:forEach items="${relateTaskList}" var="var" varStatus="vs">
												<tr>
													<td class='center'>${var.taskNo}</td>
													<td class='center'>${var.assignee}</td>
													<td class='center'>${var.status}</td>
													<td class='center'>${var.result}</td>
													<td class='center'>${var.time}</td>
													</tr>
													</c:forEach>
												</c:when>
											</c:choose>
								</tbody>
						</table>
						</div>
						
	<div class="alert alert-info">历史批复</div>
						<div>
						<table id="simple-table"
								class="table table-striped table-bordered table-hover"
								style="margin-top: 5px;">
								<thead>
									<tr>
										<th class="center" width="20%">批复人</th>
										<th class="center" width="80%">批复信息</th>
									</tr>
								</thead>

								<tbody>
									<!-- 开始循环 -->
									<c:choose>
										<c:when test="${not empty commentList}">
											<c:forEach items="${commentList}" var="var" varStatus="vs">
												<tr>
													<td class='center' width="20%">${var.userId}</td>
													<td class='center' width="80%">${var.fullMessage}</td>
													</tr>
													</c:forEach>
												</c:when>
											</c:choose>
								</tbody>
	</table>
	</div>
		<!-- 返回顶部 -->
		<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
			<i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
		</a>
	<!-- /.main-container -->

	<!-- basic scripts -->
	<!-- 页面底部js¨ -->
	<%@ include file="../system/index/foot.jsp"%>
	<!-- 删除时确认窗口 -->
	<script src="static/ace/js/bootbox.js"></script>
	<!-- ace scripts -->
	<script src="static/ace/js/ace/ace.js"></script>
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
	<script type="text/javascript">
	$(top.hangge());//关闭加载状态
	//检索
	function tosearch(){
		top.jzts();
		$("#Form").submit();
	}
	$(function() {
		//复选框全选控制
		var active_class = 'active';
		$('#simple-table > thead > tr > th input[type=checkbox]').eq(0).on('click', function(){
			var th_checked = this.checked;//checkbox inside "TH" table header
			$(this).closest('table').find('tbody > tr').each(function(){
				var row = this;
				if(th_checked) $(row).addClass(active_class).find('input[type=checkbox]').eq(0).prop('checked', true);
				else $(row).removeClass(active_class).find('input[type=checkbox]').eq(0).prop('checked', false);
			});
		});
	});
	
	

	</script>

</body>
</html>