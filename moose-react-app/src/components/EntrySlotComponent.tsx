import {WorkshopEntrySlotType} from "../ServerTypes";
import WorkshopEntryComponent from "./WorkshopEntryComponent";
import {useEffect, useState} from "react";
import {Button} from "react-bootstrap";

interface EntrySlotComponentProps {
    workshopEntrySlot: WorkshopEntrySlotType,
    accessToken:string
}

const EntrySlotComponent = ({workshopEntrySlot,accessToken}:EntrySlotComponentProps) => {
    const [showList,setShowList] = useState<boolean>(false);

    return (<div>

          <h2>{workshopEntrySlot.entryName}&nbsp;
              {showList && <Button variant={"dark"} size={"sm"} onClick={() => setShowList(false)}>Hide</Button>}
              {!showList && <Button variant={"info"} size={"sm"} onClick={() => setShowList(true)}>Show</Button>}
          </h2>

        {showList && workshopEntrySlot.workshopList.map(workshopEntry =>
            <WorkshopEntryComponent workshopEntryInfo={workshopEntry} accessToken={accessToken} key={workshopEntry.workshopid}></WorkshopEntryComponent>)}
    </div>)
};

export default EntrySlotComponent;