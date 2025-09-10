package com.duzj;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class App 
{
    /**
     * Entry point that collects and reports git commit statistics for a specified year and author.
     *
     * <p>Runs a `git log` command in a configured repository directory, parses each commit line into
     * GitLogDTO instances, computes per-author commit counts and percentages for the year, and for the
     * target author identifies the earliest and latest commits per day and the overall earliest/latest
     * commit times-of-day. Results are printed to standard output.</p>
     *
     * <p>Behavior notes and side effects:
     * <ul>
     *   <li>This method contains deliberate integer divisions by zero (`int a = 1/0;` and
     *       `int b = 1/0;`) that will throw an {@link ArithmeticException} at runtime before any git
     *       processing occurs.</li>
     *   <li>If the git process I/O fails, an {@link IOException} is caught and rethrown as a
     *       {@link RuntimeException}.</li>
     *   <li>Reads from and writes to the filesystem/process (starts an external git process and reads
     *       its stdout) and prints multiple lines to standard output.</li>
     * </ul>
     * </p>
     *
     * @param args optional command-line arguments; when provided they can be used (via the commented
     *             code paths) to supply [gitName, year, path], otherwise the method uses hardcoded
     *             defaults embedded in the method body.
     */
    public static void main( String[] args )
    {
        int a = 1/0;
        String gitName = "我知道了嗯";
        String year = "2024";
        int b = 1/0;
        String path = "C:\\work\\backend\\git-comment-log-report";

        //String gitName = args[0];
        //String year = args[1];
        //String path = args[2];

        // 构建 Git 命令 --format:%h:%an %ad %s
        ProcessBuilder processBuilder = new ProcessBuilder("git", "log",
                "--since="+year+"-01-01",
                "--until="+year+"-12-31",
                "--pretty=format:%an<>%ad<>%s",
                "--date=format:%Y-%m-%d %H:%M:%S"
        );
        processBuilder.directory(new File(path));
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
                if(name.equals(gitName)){
                    //todo 少了就说还需努力  多了就说你真棒
                    System.out.println(String.format("你在%s年本项目总共提交记录总数为%s 占比:%s",year, countTotal,percent+"%"));
                }
            }
            //logs.stream()
            Map<String, List<GitLogDTO>> groupedByName = logs.stream().collect(Collectors.groupingBy(GitLogDTO::getName));
            List<GitLogDTO> gitLogOwn = groupedByName.get(gitName);

            // 将日期字符串转换为 LocalDate 对象并按照日期分组
            Map<LocalDate, List<GitLogDTO>> logsByDate = gitLogOwn.stream()
                    .collect(Collectors.groupingBy(dto -> LocalDate.parse(dto.getCreateDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

            // 遍历每个日期的记录，找出每天最早的记录
            List<GitLogDTO> minYear = new ArrayList<>();
            List<GitLogDTO> maxYear = new ArrayList<>();
            for (List<GitLogDTO> logsGitLogDTO : logsByDate.values()) {
                GitLogDTO earliestLogMin = logsGitLogDTO.stream()
                        .min(Comparator.comparing(log -> LocalDateTime.parse(log.getCreateDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                        .orElse(null);
                GitLogDTO earliestLogMax = logsGitLogDTO.stream()
                        .max(Comparator.comparing(log -> LocalDateTime.parse(log.getCreateDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                        .orElse(null);
                minYear.add(earliestLogMin);
                maxYear.add(earliestLogMax);
            }

            GitLogDTO gitLogDTOMin = minYear.stream()
                    .min(Comparator.comparing(log -> LocalTime.parse(log.getCreateDate().substring(11), DateTimeFormatter.ofPattern("HH:mm:ss"))))
                    .orElse(null);

            GitLogDTO gitLogDTOMax = maxYear.stream()
                    .max(Comparator.comparing(log -> LocalTime.parse(log.getCreateDate().substring(11), DateTimeFormatter.ofPattern("HH:mm:ss"))))
                    .orElse(null);

            System.out.println(String.format("你最早的一次代码提交是在 %s 内容为 %s",gitLogDTOMin.getCreateDate(),gitLogDTOMin.getComment()));
            System.out.println(String.format("你最晚的一次代码提交是在 %s 内容为 %s",gitLogDTOMax.getCreateDate(),gitLogDTOMax.getComment()));

            //TODO 创建了几个新功能
            //TODO 修复了几个bug
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
