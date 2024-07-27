import {Alert, Button, Stack} from "react-bootstrap";
import {CancelRegistrationInput, RegistrationStatus} from "../ServerTypes";
import {useState} from "react";
import cancelRegistrationToServer from "../hooks/cancelRegistrationToServer";

interface WorkshopCancellationProps {
    accessToken: string|null;
    registrationId: string;
    registrationStatus: RegistrationStatus;
    onRegistrationCancelled:(registrationStatus:RegistrationStatus)=>void
}

const WorkshopCancellation: React.FC<WorkshopCancellationProps> = ({registrationId,accessToken,registrationStatus,onRegistrationCancelled}) => {
    const [confirmCancel,setConfirmCancel] = useState<boolean>(false);
    const [errormessage,setErrormessage] = useState<string|null>(null);
    const [showConfirmMessage,setShowConfirmMessage] = useState<boolean>(false);

    const onCancelClick = () => {
        setConfirmCancel(true);
    }

    const onConfirmCancelledClick = () => {
        const ci:CancelRegistrationInput = {
            registrationId: registrationId,
            accessToken: accessToken
        }
        cancelRegistrationToServer(ci).then((cancelRegistratioToServerResult) => {
            if (cancelRegistratioToServerResult.errormessage) {
                setErrormessage(cancelRegistratioToServerResult.errormessage);
            }
            if (cancelRegistratioToServerResult.cancelRegistrationOutput) {
                onRegistrationCancelled(cancelRegistratioToServerResult.cancelRegistrationOutput.registrationStatus);
                setShowConfirmMessage(true);
            }
        });
    }

    return (<div>
        {(registrationStatus === RegistrationStatus.REGISTERED) && <p>You are registered on this workshop</p>}
        {(registrationStatus === RegistrationStatus.WAITING) && <p>You are on the waiting list for this workshop</p>}
        {errormessage && <Alert variant="danger">{errormessage}</Alert>}
        {(!errormessage && !showConfirmMessage && !confirmCancel) && <Button variant={"danger"} onClick={onCancelClick}>Cancel registration</Button>}
        {(!errormessage && !showConfirmMessage && confirmCancel) && <Stack direction={"horizontal"} gap={2} >
            <p>Are you sure you want to cancel?</p>
            <Button variant={"danger"} onClick={onConfirmCancelledClick}>Confirm cancellation</Button>
        </Stack>}
        {showConfirmMessage && <Alert variant={"info"}>Registration has been canceled</Alert>}

    </div>);
};

export default WorkshopCancellation