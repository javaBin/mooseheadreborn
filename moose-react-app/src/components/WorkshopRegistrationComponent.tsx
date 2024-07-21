import {AddParticipantInput, WorkshopType} from "../ServerTypes";
import RegisterParticipant from "./RegisterParticipant";
import WorkshopDisplay from "./WorkshopDisplay";
import {useEffect, useState} from "react";
import readWorkshopFromServer from "../hooks/readWorkshopFromServer";

interface WorkshopInfoProps {
    workshopId:string,
    accessToken:string|null,
}
const WorkshopRegistrationComponent: React.FC<WorkshopInfoProps> = ({workshopId,accessToken}) => {
    const [workshop, setWorkshop] = useState<WorkshopType>();

    useEffect(() => {
        readWorkshopFromServer(workshopId).then(resultFromServer => {
                if (resultFromServer.workshop) {
                    setWorkshop(resultFromServer.workshop);
                }
            }
        )
    }, []);

    return (<div>
        <h1>Registration</h1>
        {workshop && <WorkshopDisplay workshop={workshop} displayLink={false}/>}

        <div>
            <RegisterParticipant/>
        </div>
    </div>);
}

export default WorkshopRegistrationComponent;