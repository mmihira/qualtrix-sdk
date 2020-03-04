package qualtrix;

class EndPoints {
  public static class V3 {

    static class WhoAmI {
      static String path() {
        return "API/v3/whoami";
      }
    }

    static class CreateSurvey {
      static String path() {
        return "API/v3/survey-definitions";
      }
    }

    static class DeleteSurvey {
      static String forSurvey(String surveyId) {
        return String.format("API/v3/survey-definitions/%s", surveyId);
      }
    }

    static class ListSurveys {
      static String path() {
        return "API/v3/surveys";
      }
    }

    static class GetSurvey {
      static String forSurvey(String surveyId) {
        return String.format("%s/%s", "API/v3/surveys", surveyId);
      }
    }

    static class UpdateSurveyFlow {
      static String forSurvey(String surveyId) {
        return String.format("%s/%s", "API/v3/survey-definitions/%s/flow", surveyId);
      }
    }

    static class CreateResponseExport {

      static String forSurvey(String surveyId) {
        return String.format("%s/%s/%s", "API/v3/surveys", surveyId, "export-responses");
      }
    }

    static class ResponseExportProgress {
      static String path(String surveyId, String exportProgressId) {
        return String.format(
            "%s/%s/%s/%s", "API/v3/surveys", surveyId, "export-responses", exportProgressId);
      }
    }

    static class ResponseExportFile {
      static String path(String surveyId, String fileId) {
        return String.format(
            "%s/%s/%s/%s/%s", "API/v3/surveys", surveyId, "export-responses", fileId, "file");
      }
    }

    static class CreateMailingList {
      static String path() {
        return "API/v3/mailinglists";
      }
    }

    static class ListMailingLists {
      static String path() {
        return "API/v3/mailinglists";
      }
    }

    static class CreateContact {
      static String path(String mailingListId) {
        return String.format("%s/%s/%s", "API/v3/mailinglists", mailingListId, "contacts");
      }
    }

    static class DeleteContact {
      static String path(String mailingListId, String contactId) {
        return String.format(
            "%s/%s/%s/%s", "API/v3/mailinglists", mailingListId, "contacts", contactId);
      }
    }

    static class ListContacts {
      static String path(String mailingListId) {
        return String.format("%s/%s/%s", "API/v3/mailinglists", mailingListId, "contacts");
      }
    }

    static class GenerateDistributionLinks {
      static String path() {
        return "API/v3/distributions";
      }
    }

    static class DeleteDistribution {
      static String path(String distributionId) {
        return String.format("%s/%s", "API/v3/distributions", distributionId);
      }
    }

    static class DeleteMailingList {
      static String path(String mailingListId) {
        return String.format("%s/%s", "API/v3/mailinglists", mailingListId);
      }
    }

    static class GetMailingList {
      static String path(String mailingListId) {
        return String.format("%s/%s", "API/v3/mailinglists", mailingListId);
      }
    }

    static class RetrieveGeneratedLinks {
      static String path(String distributionId) {
        return String.format(
            "%s/%s/%s?surveyId={surveyId}", "API/v3/distributions", distributionId, "links");
      }

      static String path(String distributionId, String surveyId) {
        return String.format(
            "%s/%s/%s?surveyId=%s", "API/v3/distributions", distributionId, "links", surveyId);
      }
    }
  }
}
