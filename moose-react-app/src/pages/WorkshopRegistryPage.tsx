import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import WorkshopRegistrationComponent from "../components/WorkshopRegistrationComponent";

function WorkshopRegistryPage() {
    const { workshopId} = useParams<{workshopId: string}>();
    const accessToken = window.localStorage.getItem("accessToken");

    if (workshopId) {
        window.localStorage.setItem("currentWorkshopId", workshopId);
    }


    return (<Container>
        {workshopId && <WorkshopRegistrationComponent workshopId={workshopId} accessToken={accessToken}/>}
    </Container>);
}

export default WorkshopRegistryPage;