import {Alert, Container} from "react-bootstrap";
import {useContext, useEffect, useState} from "react";
import {AppContext} from "../context/AppContext";
import {UserLogin, WorkshopEntrySlotType} from "../ServerTypes";
import readWorkshopEntryList from "../hooks/readWorkshopEntryList";
import EntrySlotComponent from "../components/EntrySlotComponent";

const EntryRegistrationPage = () => {
    const appContext = useContext(AppContext);
    const userLogin:UserLogin|null = appContext?.userLogin || null;
    const [workshopSlotList,setWorkshopSlotsList] = useState<WorkshopEntrySlotType[]>([]);
    const [errormessage,setErrormessage] = useState<string|null>(null);

    const accessToken:string|null = userLogin?.accessToken || null;

    useEffect(() => {
        if (!userLogin?.accessToken) {
            setWorkshopSlotsList([]);
            return
        }
        readWorkshopEntryList(userLogin.accessToken)
            .then(allWorkshopEntryFromServer => setWorkshopSlotsList(allWorkshopEntryFromServer.slotList))
            .catch(errorFromServer => setErrormessage(errorFromServer));
    }, [userLogin]);

    return (<Container>
        <h1>Entry registration</h1>
        {errormessage && <Alert variant={"danger"}>{errormessage}</Alert> }
        {accessToken && workshopSlotList.map(workshopSlot =>
            <EntrySlotComponent accessToken={accessToken} workshopEntrySlot={workshopSlot} key={workshopSlot.entryName}></EntrySlotComponent>)}
    </Container>)
};

export default EntryRegistrationPage;