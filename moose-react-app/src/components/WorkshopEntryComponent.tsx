import {
    EntryRegistrationForWorkshopType, ReadEntryRegistrationInputType,
    UpdateCheckinInputType,
    WorkshopEntryInfoType,
    WorkshopEntryRegistrationType
} from "../ServerTypes";
import {useEffect, useState} from "react";
import {Alert, Button, Form, ListGroup} from "react-bootstrap";
import readEntriesForWorkshop from "../hooks/readEntriesForWorkshop";
import ParticipantWorkshopEntryComponent from "./ParticipantWorkshopEntryComponent";
import setCheckinOnServer from "../hooks/setCheckinOnServer";
import readCheckinFromServer from "../hooks/readCheckinFromServer";

interface WorkshopEntryComponentProps {
    workshopEntryInfo: WorkshopEntryInfoType;
    accessToken: string;
}
const WorkshopEntryComponent = ({workshopEntryInfo,accessToken}:WorkshopEntryComponentProps) => {
    const [showDetails, setShowDetails] = useState<boolean>(false)
    const [entryRegistrationFromServerList, setEntryRegistrationFromServerList] = useState<WorkshopEntryRegistrationType[]>([]);
    const [entryRegistrationList,setEntryRegistrationList] = useState<WorkshopEntryRegistrationType[]>([]);
    const [errormessage,setErrormessage] = useState<string|null>(null);
    const [filterText, setFilterText] = useState<string>("");
    const [numCheckedIn, setNumCheckedIn] = useState<number>(0);

    useEffect(() => {
        readEntriesForWorkshop({
            accessToken: accessToken,
            workshopId: workshopEntryInfo.workshopid
        }).then(entryRegistrationForWorkshop=>{
            setEntryRegistrationFromServerList(entryRegistrationForWorkshop.entryList);
            setEntryRegistrationList(entryRegistrationForWorkshop.entryList);
            setFilterText("");
            setNumCheckedIn(entryRegistrationForWorkshop.numberCheckedIn)

        }).catch(errorFromServer => setErrormessage(errorFromServer));
    },[]);

    const handleFilterChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setFilterText(event.target.value);
    };

    useEffect(() => {
        setEntryRegistrationList(entryRegistrationFromServerList.filter(entry => {
            if (filterText.length === 0) {
                return true;
            }
            const pos = entry.participantName.toLowerCase().indexOf(filterText.toLowerCase());
            if (pos !== -1) {
                return true;
            }
            const emailpos = entry.participantEmail.toLowerCase().indexOf(filterText.toLowerCase());
            return (emailpos !== -1);

        }));
    }, [filterText,entryRegistrationFromServerList]);

    const updateCheckin = (registrationId:string,checkinFlag:boolean) => {
        console.log("UpdateCheckin " + registrationId + " to " + checkinFlag);
        const updateCheckinInput:UpdateCheckinInputType = {
            accessToken: accessToken,
            registrationId:registrationId,
            setCheckinTo: checkinFlag
        };
        setCheckinOnServer(updateCheckinInput).then(entryRegistrationForWorkshop => {
            setEntryRegistrationFromServerList(entryRegistrationForWorkshop.entryList);
            setNumCheckedIn(entryRegistrationForWorkshop.numberCheckedIn)
        }).catch(errorFromServer => setErrormessage(errorFromServer));

    }
    const reglist:WorkshopEntryRegistrationType[] = (showDetails) ? entryRegistrationList : [];

    const onReload = () => {
        const readEntryRegistrationInput:ReadEntryRegistrationInputType = {
            workshopId: workshopEntryInfo.workshopid,
            accessToken: accessToken
        };
        readCheckinFromServer(readEntryRegistrationInput).then(entryRegistrationForWorkshop => {
            setEntryRegistrationFromServerList(entryRegistrationForWorkshop.entryList);
            setNumCheckedIn(entryRegistrationForWorkshop.numberCheckedIn);
        })
    };

    return (<div>
        <h3>{workshopEntryInfo.workshopName}&nbsp;
            {showDetails && <Button variant={"dark"} size={"sm"} onClick={() => setShowDetails(false)}>Hide</Button>}
            {!showDetails && <Button variant={"info"} size={"sm"} onClick={() => setShowDetails(true)}>Show</Button>}
        </h3>
        {errormessage && <Alert variant={"danger"}>{errormessage}</Alert>}
        {showDetails && <p>Registered: {workshopEntryInfo.numRegistred} Waiting: {workshopEntryInfo.numWaiting} Checked in {numCheckedIn}&nbsp;<Button variant={"info"} onClick={onReload}>Reload</Button></p>}
        {showDetails && <Form.Group className={"mb-2"} style={{maxWidth: "300px"}}>
            <Form.Control type={"text"} value={filterText} onChange={handleFilterChange} placeholder={"Filter by"}></Form.Control>
        </Form.Group>}

            <ListGroup>
            {reglist.map(er => <ParticipantWorkshopEntryComponent workshopEntryRegistrationType={er} key={er.registrationId} onSetCheckin={updateCheckin}/>)}
        </ListGroup>
    </div>);
}

export default WorkshopEntryComponent;