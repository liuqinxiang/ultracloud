<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="en">
	<head>
	<base href="<%=basePath%>">
	<!-- jsp文件头和头部 -->
	<%@ include file="../system/index/top.jsp"%>
	<script type="text/javascript" src="static/ace/js/jquery.js"></script>
</head>
<body class="no-skin">
<!-- /section:basics/navbar.layout -->
<div class="main-container" id="main-container">
	<!-- /section:basics/sidebar -->
	<div class="main-content">
		<div class="main-content-inner">
			<div class="page-content">
				<div class="row">
					<div class="col-xs-12">
					
					<form action="autoinstallrule/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" value="no" id="hasTp1" />
						<input type="hidden" name="id" id="id" value="${pd.id}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:79px;text-align: right;padding-top: 13px;"><i class="ace-icon fa fa-asterisk red"></i>&nbsp;环境:</td>
								<td id="cluster_td">
									<select class="chosen-select form-control" name="cluster_id" id="cluster_id" style="vertical-align:top;" style="width:98%;" >
										<c:forEach items="${clusterList}" var="var">
											<option value="${var.id }" <c:if test="${var.id  == pd.cluster_id }">selected</c:if>>${var.name }</option>
										</c:forEach>
									</select>
								</td>
							</tr>
							<c:if test="${pd.type == 'vmware' }">
								<tr>
									<td style="width:130px;text-align: right;padding-top: 13px;"><i class="ace-icon fa fa-asterisk red"></i>&nbsp;计算规则:</td>
									<td id="js">
										<select class="chosen-select form-control" name="num_rule" id="num_rule"  style="vertical-align:top;" style="width:98%;" >
										<c:forEach items="${autoinstall_rule_num_List}" var="dictionaries">
											<option value="${dictionaries.BIANMA }" <c:if test="${dictionaries.BIANMA == pd.num_rule }">selected</c:if>>${dictionaries.NAME }</option>
										</c:forEach>
										</select>
									</td>
								</tr>
								<tr>
									<td style="width:130px;text-align: right;padding-top: 13px;"><i class="ace-icon fa fa-asterisk red"></i>&nbsp;存储规则:</td>
									<td id="js">
										<select class="chosen-select form-control" name="storage_rule" id="storage_rule"  style="vertical-align:top;" style="width:98%;" >
										<c:forEach items="${autoinstall_rule_storage_List}" var="dictionaries">
											<option value="${dictionaries.BIANMA }" <c:if test="${dictionaries.BIANMA == pd.storage_rule }">selected</c:if>>${dictionaries.NAME }</option>
										</c:forEach>
										</select>
									</td>
								</tr>
								<tr>
									<td style="width:130px;text-align: right;padding-top: 13px;"><i class="ace-icon fa fa-asterisk red"></i>&nbsp;IP规则:</td>
									<td id="js">
										<select class="chosen-select form-control" name="ip_rule" id="ip_rule"  style="vertical-align:top;" style="width:98%;" >
										<c:forEach items="${autoinstall_rule_ip_List}" var="dictionaries">
											<option value="${dictionaries.BIANMA }" <c:if test="${dictionaries.BIANMA == pd.ip_rule }">selected</c:if>>${dictionaries.NAME }</option>
										</c:forEach>
										</select>
									</td>
								</tr>
							</c:if>
							<c:if test="${pd.type == 'OpenStack' }">
								<tr>
									<td style="width:130px;text-align: right;padding-top: 13px;"><i class="ace-icon fa fa-asterisk red"></i>&nbsp;规则:</td>
									<td id="js">
										<select class="chosen-select form-control" name="openstack_rule" id="openstack_rule"  style="vertical-align:top;" style="width:98%;" >
										<c:forEach items="${autoinstall_rule_openstack_List}" var="dictionaries">
											<option value="${dictionaries.BIANMA }" <c:if test="${dictionaries.BIANMA == pd.openstack_rule }">selected</c:if>>${dictionaries.NAME }</option>
										</c:forEach>
										</select>
									</td>
								</tr>
							</c:if>
							<tr>
								<td style="text-align: center;" colspan="10">
									<a class="btn btn-mini btn-primary" onclick="save();">保存</a>
									<a class="btn btn-mini btn-danger" onclick="top.Dialog.close();">取消</a>
								</td>
							</tr>
						</table>
						</div>
						<div id="zhongxin2" class="center" style="display:none"><br/><br/><br/><br/><br/><img src="static/images/jiazai.gif" /><br/><h4 class="lighter block green">提交中...</h4></div>
					</form>
	
					<div id="zhongxin2" class="center" style="display:none"><img src="static/images/jzx.gif" style="width: 50px;" /><br/><h4 class="lighter block green"></h4></div>
					</div>
					<!-- /.col -->
				</div>
				<!-- /.row -->
			</div>
			<!-- /.page-content -->
		</div>
	</div>
	<!-- /.main-content -->
</div>
<!-- /.main-container -->


	<!-- 页面底部js¨ -->
	<%@ include file="../system/index/foot.jsp"%>
	<!-- 下拉框 -->
	<script src="static/ace/js/chosen.jquery.js"></script>
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
		<script type="text/javascript">
		$(top.hangge());
		$(function() {
			var str1 = '<c'+':if test="'+'$'+'{QX.'+$("#QX_NAME").val();
			var str2 = ' == 1 }">这里放按钮<'+'/c:'+'if>';
			$("#code").val(str1+str2);
		});
		//保存
		function save(){
			if($("#cluster_id").val()=="" || $("#cluster_id").val()==null){
				$("#cluster_td").tips({
					side:3,
		            msg:'请选择环境',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#cluster_id").focus();
			return false;
			}
			
			$("#Form").submit();
			$("#zhongxin").hide();
			$("#zhongxin2").show();
		}

		</script>
</body>
</html>