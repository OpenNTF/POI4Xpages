package biz.webgate.dominoext.poi.component.kernel.simpleviewexport;

import javax.servlet.http.HttpServletResponse;

import biz.webgate.dominoext.poi.component.containers.UISimpleViewExport;

public interface IExportProcessor {

	public void process2HTTP(ExportModel expModel, UISimpleViewExport uis, HttpServletResponse hsr);
}
