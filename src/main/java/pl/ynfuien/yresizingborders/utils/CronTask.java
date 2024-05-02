package pl.ynfuien.yresizingborders.utils;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.ZonedDateTime;
import java.util.Locale;

public class CronTask {
    private static CronDefinition cronDefinition = CronDefinitionBuilder.defineCron()
            .withSeconds().and()
            .withMinutes().and()
            .withHours().and()
            .withDayOfMonth().supportsHash().supportsL().supportsW().and()
            .withMonth().and()
            .withDayOfWeek().withIntMapping(7, 0).supportsHash().supportsL().supportsW().and()
            .instance();

    private Cron cron = null;
    private final String cronExpression;
    private ExecutionTime executionTime = null;
    private ZonedDateTime nextExecution = null;


    public CronTask(String cronExpression) {
        this.cronExpression = cronExpression;
    }


    public boolean validate() {
        CronParser parser = new CronParser(cronDefinition);
        try {
            if (cronExpression == null) throw new NullPointerException("Cron expression can't be null!");
            cron = parser.parse(cronExpression);
            cron.validate();
        } catch (IllegalArgumentException|NullPointerException e) {
            e.printStackTrace();
            return false;
        }

        executionTime = ExecutionTime.forCron(cron);
        nextExecution = executionTime.nextExecution(ZonedDateTime.now()).get();
        return true;
    }

    public boolean checkTime() {
        // Return false if next execution is after now
        ZonedDateTime now = ZonedDateTime.now();
        if (nextExecution.isAfter(now)) return false;

        nextExecution = executionTime.nextExecution(now).get();
        return true;

    }

    public static boolean validateExpression(String expression) {
        if (expression == null) return false;

        CronParser parser = new CronParser(cronDefinition);
        try {
            Cron cron = parser.parse(expression);
            cron.validate();
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    // Getters
    public Cron getCron() {
        return cron;
    }

    public String getDescription() {
        return CronDescriptor.instance(Locale.US).describe(cron);
    }

    public String getExpression() {
        return cronExpression;
    }

}
