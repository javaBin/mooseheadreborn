import {render, screen} from "@testing-library/react";
import WorkshopCancellation from "./WorkshopCancellation";
import {CancelRegistrationInput, RegistrationStatus} from "../ServerTypes";
import user from "@testing-library/user-event";

let serverInput:CancelRegistrationInput|null = null;

jest.mock("../hooks/cancelRegistrationToServer",() => {
    return (cancelRegistrationInput:CancelRegistrationInput) => {
        serverInput = cancelRegistrationInput;
        return new Promise((resolve,reject) => {
            const result = {
                registrationStatus: "NOT_REGISTERED",
            };
            resolve(result);
        })
    }
});

test('should be avble to cancel',async () => {
    const registationId = "myRegistrationId";
    const onCancelledMock:(register:RegistrationStatus) => void = jest.fn();
    render(<WorkshopCancellation accessToken={null} registrationId={registationId} registrationStatus={RegistrationStatus.REGISTERED} onRegistrationCancelled={onCancelledMock}/>);
    expect(screen.getByText("You are registered on this workshop")).toBeInTheDocument();
    const cancelButton = screen.getByRole("button");
    expect(cancelButton).toBeInTheDocument();
    expect(cancelButton).toHaveTextContent("Cancel registration");
    user.click(cancelButton);
    const confirmCancelButton = await screen.findByRole("button");
    expect(confirmCancelButton).toBeInTheDocument();
    expect(confirmCancelButton).toHaveTextContent("Confirm cancellation");

    user.click(confirmCancelButton);

    const confirmationAlert = await screen.findByRole("alert")
    expect(confirmationAlert).toBeInTheDocument();
    expect(serverInput).toEqual({
        accessToken: null,
        registrationId: registationId
    });
    expect(onCancelledMock).toHaveBeenCalledWith(RegistrationStatus.NOT_REGISTERED);

});