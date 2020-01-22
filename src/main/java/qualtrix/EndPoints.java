package qualtrix;

class EndPoints {
    public static class V3 {
        static class WhoAmI {
            static String path() {
                return "API/v3/whoami";
            }
        }
        static class ListSurveys{
            static String path() {
                return "API/v3/surveys";
            }
        }
        static class GetSurvey{
            static String forSurvey(String surveyId) {
                return String.format("%s/%s", "API/v3/surveys/", surveyId);
            }
        }
        static class CreateResponseExport{
            static String forSurvey(String surveyId) {
                return String.format("%s/%s/%s", "API/v3/surveys/", surveyId, "export-responses");
            }
        }
    }
}
