package com.ats.adminpanel.commons;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ats.adminpanel.model.Info;
import com.ats.adminpanel.model.accessright.ModuleJson;

public class AccessControll {

	public static Info checkAccess(String currentMapping, String viewMapping, String view, String add, String edit,
			String delete, List<ModuleJson> newModuleList) {

		Info info = new Info();

		try {

			System.out.println("in function newModuleList " + newModuleList+"viewMapping"+viewMapping);

			int viewMappingFound = 0;

			for (int i = 0; i < newModuleList.size(); i++) {

				for (int j = 0; j < newModuleList.get(i).getSubModuleJsonList().size(); j++) {

					if (newModuleList.get(i).getSubModuleJsonList().get(j).getSubModuleMapping().equals(viewMapping)) {
						viewMappingFound = 1;

						System.out.println("Mapping Found");

						if (view.equals("1")) {

							System.out.println("in view");
							info.setError(false);
							info.setMessage("Have Access");

						} else if (add.equals("1")) {

							System.out.println("in add");

							if (newModuleList.get(i).getSubModuleJsonList().get(j).getAddApproveConfig().equals(add)) {
								System.out.println("in add if");
								info.setError(false);
								info.setMessage("Have Access");
							} else {
								System.out.println("in add else");
								info.setError(true);
								info.setMessage("Access denied");
							}
						} else if (edit.equals("1")) {

							System.out.println("in edit");
							if (newModuleList.get(i).getSubModuleJsonList().get(j).getEditReject().equals(edit)) {
								System.out.println("in edit if");
								info.setError(false);
								info.setMessage("Have Access");
							} else {
								System.out.println("in edit else");
								info.setError(true);
								info.setMessage("Access denied");
							}
						} else if (delete.equals("1")) {
							System.out.println("in delete");
							if (newModuleList.get(i).getSubModuleJsonList().get(j).getDeleteRejectApprove()
									.equals(delete)) {
								System.out.println("in edit if");
								info.setError(false);
								info.setMessage("Have Access");
							} else {
								System.out.println("in edit else");
								info.setError(true);
								info.setMessage("Access denied");
							}
						} else {
							System.out.println("in else");
							info.setError(true);
							info.setMessage("Access denied");
						}

					}
				}

			}

			if (viewMappingFound == 0) {
				System.out.println("mapping not found");
				info.setError(true);
				info.setMessage("Access Denied");
			}

		} catch (Exception e) {
			e.printStackTrace();
			info.setError(true);
			info.setMessage("access denied ");
		}

		return info;

	}

}
