package com.duzj;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        // 构建 Git 命令 --format:%h:%an %ad %s
        ProcessBuilder processBuilder = new ProcessBuilder("git", "log",
                "--since=2023-01-01",
                "--until=2023-12-31",
                "--pretty=format:%an<>%ad<>%s",
                "--date=format:%Y-%m-%d %H:%M:%S"
        );
        processBuilder.directory(new File("/Users/duzengjie/IdeaProjects/demo-springboot"));
        processBuilder.redirectErrorStream(true);
        System.out.println(String.join(" ", processBuilder.command()));
        // 执行命令
        try {
            List<GitLogDTO> logs = new ArrayList<GitLogDTO>();
            Process process = processBuilder.start();

            // 读取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("<>");
                System.out.println(line);
                GitLogDTO gitLogDTO = new GitLogDTO();
                gitLogDTO.setName(split[0]);
                gitLogDTO.setCreateDate(split[1]);
                gitLogDTO.setComment(split[2]);
                logs.add(gitLogDTO);
            }

            String yearCountTotal = logs.size()+"";
            System.out.println(( String.format("2023年本项目总共提交记录总数为:%s", yearCountTotal)));
            Map<String, Long> collect = logs.stream().collect(Collectors.groupingBy(GitLogDTO::getName, Collectors.counting()));
            for (String name : collect.keySet()) {
                String countTotal = collect.get(name)+"";
                BigDecimal percent = new BigDecimal(countTotal).divide(new BigDecimal(yearCountTotal), 2,RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                System.out.println(String.format("%s在2023年本项目总共提交记录总数为%s 占比:%s", name, countTotal,percent+"%"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
