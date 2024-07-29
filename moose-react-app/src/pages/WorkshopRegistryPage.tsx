import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import WorkshopRegistrationComponent from "../components/WorkshopRegistrationComponent";
import {useContext} from "react";
import {AppContext} from "../context/AppContext";
import {UserLogin} from "../ServerTypes";

function WorkshopRegistryPage() {
    const appContext = useContext(AppContext);
    const userLogin:UserLogin|undefined = appContext?.userLogin

    const { workshopId} = useParams<{workshopId: string}>();
    const accessToken = userLogin?.accessToken || null;

    if (workshopId) {
        window.localStorage.setItem("currentWorkshopId", workshopId);
    }


    return (<Container>
        {workshopId && <WorkshopRegistrationComponent workshopId={workshopId} accessToken={accessToken}/>}
    </Container>);
}

export default WorkshopRegistryPage;