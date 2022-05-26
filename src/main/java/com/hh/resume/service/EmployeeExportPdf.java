package com.hh.resume.service;

import com.hh.resume.dao.Education;
import com.hh.resume.dao.Employee;
import com.hh.resume.helper.DateHelper;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfBoxRenderer;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.render.Box;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EmployeeExportPdf {

    private static final int PDF_DOTS_PER_PIXEL = 20;
    public void pdfExport(HttpServletResponse response, Employee employee){
        File file  = new File("src/main/resources/templates/views/srt-resume.html");
        File inputHTML  = new File(file.getAbsolutePath());

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useDefaultPageSize(220f,297f, BaseRendererBuilder.PageSizeUnits.MM);

        // Note: We set a huge page size so it can auto-size the div without constraints.
        // Note: We must set a width on the div, otherwise it will be the width of the page.
        String html = "";

        if (employee != null) {
            try {
                html = Files.readString(Paths.get(inputHTML.toURI()));

                html = setProperties(html, employee);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        builder.withHtmlContent(html, /* Base url */ null);
        PdfBoxRenderer renderer = builder.buildPdfRenderer();
        renderer.layout();

        // The root box is <html>, the first child is <body>, then <div>.
        Box box = renderer.getRootBox().getChild(0).getChild(0);

        // Replace the original page size with the size of the <div> box.
        // Note the +1 to make sure it fits, could also try Math.ceil
        html = html.replace("@page { size: 10000px 10000px; }",
                "@page { size: " + ((box.getWidth() / PDF_DOTS_PER_PIXEL)/(2620/290)) + "px "
                        + ((box.getHeight() / PDF_DOTS_PER_PIXEL) )+ "px; }");

        // Now output the new html.
        try (OutputStream os = response.getOutputStream()) {
            PdfRendererBuilder builder2 = new PdfRendererBuilder();
            builder2.withHtmlContent(html, null);
            builder2.toStream(os);
            try {
                builder2.run();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String setProperties(String html, Employee employee) {
        String headInfo = String.format("<div class=\"yui-u first\">\n" +
                "\t\t\t\t\t<h1>%s %s %s</h1>\n" +
                "\t\t\t\t\t<br/>\n" +
                "\t\t\t\t\t<h2>%s</h2>\n" +
                "\t\t\t\t\t<br/>\n" +
                "\t\t\t\t\t<p>Date of Birth: %s</p>\n" +
                "\t\t\t\t\t<p>Address: %s</p>\n" +
                "\t\t\t\t</div>",
                employee.getSurname() != null ? employee.getSurname() : "",
                employee.getName(),
                employee.getPatronymic() != null ? employee.getPatronymic() : "",
                employee.getJob(),
                DateHelper.toString(employee.getBirthDate()),
                employee.getAddress());
        html = html.replace("{{{HEAD-INFO}}}", headInfo);

        if (employee.getPhotoUrl() != null) {
            String photo = String.format("<div class=\"yui-u photo\">\n" +
                    "\t\t\t\t\t<div class=\"contact-info\">\n" +
                    "\t\t\t\t\t\t<img src=\"%s\" width=\"250\" height=\"250\" alt=\"%s\" />\n" +
                    "\t\t\t\t\t</div>\n" +
                    "\t\t\t\t</div>", employee.getPhotoUrl(), employee.getSurname());

            html = html.replace("{{{EMPLOYEE-PHOTO}}}", photo);
        } else {
            html = html.replace("{{{EMPLOYEE-PHOTO}}}", "");
        }

        if (employee.getProfile() != null) {
            String profile = String.format("<div class=\"yui-gf\">\n" +
                    "\t\t\t\t\t\t<div class=\"yui-u first\">\n" +
                    "\t\t\t\t\t\t\t<h2>Profile</h2>\n" +
                    "\t\t\t\t\t\t</div>\n" +
                    "\t\t\t\t\t\t<div class=\"yui-u\">\n" +
                    "\t\t\t\t\t\t\t<p class=\"enlarge\">\n" +
                    "\t\t\t\t\t\t\t\t%s \n" +
                    "\t\t\t\t\t\t\t</p>\n" +
                    "\t\t\t\t\t\t</div>\n" +
                    "\t\t\t\t\t</div>", employee.getProfile());
            html = html.replace("{{{EMPLOYEE-PROFILE}}}", profile);
        } else {
            html = html.replace("{{{EMPLOYEE-PROFILE}}}", "");
        }

        if (employee.getSkills() != null && !employee.getSkills().isEmpty()){
            StringBuilder skills = new StringBuilder();
            skills.append("<div class=\"yui-gf\">\n" +
                    "\t\t\t\t\t\t<div class=\"yui-u first\">\n" +
                    "\t\t\t\t\t\t\t<h2>Skills</h2>\n" +
                    "\t\t\t\t\t\t</div>\n" +
                    "\t\t\t\t\t\t<div class=\"yui-u\">");
            int size = employee.getSkills().size();
            int n = size < 10 ? 3 : size % 3 == 0 ? (size / 3) : (size / 3) + 1;
            for (int i = 0; i < size; i++) {
                if ((i+1) % n == 1){
                    skills.append("<ul class=\"talent\">\n");
                }
                if ((i+1) % n != 0) {
                    skills.append(String.format("<li>%s</li>\n", employee.getSkills().get(i).getName()));
                } else {
                    skills.append(String.format("<li class=\"last\">%s</li>\n" +
                            "\t\t\t\t\t\t\t</ul>", employee.getSkills().get(i).getName()));
                }
            }

            if (size < 3 || size % n != 0){
                skills.append("\t\t\t\t\t\t\t</ul>");
            }

            skills.append("</div>\n" +
                    "\t\t\t\t\t</div>");

            html = html.replace("{{{EMPLOYEE-SKILLS}}}", skills);
        } else {
            html = html.replace("{{{EMPLOYEE-SKILLS}}}", "");
        }

        if (employee.getEducations() != null && !employee.getEducations().isEmpty()){
            StringBuilder educations = new StringBuilder("<div class=\"yui-gf\">\n" +
                    "\t\t\t\t\t\t<div class=\"yui-u first\">\n" +
                    "\t\t\t\t\t\t\t<h2>Education</h2>\n" +
                    "\t\t\t\t\t\t</div>\n" +
                    "\t\t\t\t\t\t<div class=\"yui-u\">");
            if (employee.getEducations().size() > 1) {
                for (int i = 0; i < employee.getEducations().size() - 1; i++) {
                    Education education = employee.getEducations().get(i);
                    educations.append(String.format("<div class=\"job\">\n" +
                            "\t\t\t\t\t\t\t\t<h2>%s</h2>\n" +
                            "\t\t\t\t\t\t\t\t<h3>%s</h3>\n" +
                            "\t\t\t\t\t\t\t\t<h4>%s - %s</h4>\n" +
                            "\t\t\t\t\t\t\t</div>", education.getName(), education.getBranch(), education.getBeginDate(), education.getEndDate()));
                }
            }
            Education education = employee.getEducations().get(employee.getEducations().size()-1);
            educations.append(String.format("<div class=\"job last\">\n" +
                    "\t\t\t\t\t\t\t\t<h2>%s</h2>\n" +
                    "\t\t\t\t\t\t\t\t<h3>%s</h3>\n" +
                    "\t\t\t\t\t\t\t\t<h4>%s - %s</h4>\n" +
                    "\t\t\t\t\t\t\t\t</div>\n" +
                    "\t\t\t\t\t\t</div>\n" +
                    "\t\t\t\t\t</div>",education.getName(),education.getBranch(), education.getBeginDate(), education.getEndDate()));
            html = html.replace("{{{EMPLOYEE-EDUCATIONS}}}", educations);
        } else {
            html = html.replace("{{{EMPLOYEE-EDUCATIONS}}}", "");
        }
        return html;
    }
}
